/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import connectors.AuthConnector
import fixtures.{AuthFixture, MetadataFixture}
import helpers.SCRSSpec
import models.{MetadataResponse, Metadata}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.MetadataService
import play.api.mvc.Results.{Ok, Created}
import play.api.test.Helpers._

class MetadataControllerSpec extends SCRSSpec with MetadataFixture with AuthFixture {

  class Setup {
    val controller = new MetadataController {
      override val metadataService: MetadataService = mockMetadataService
      override val auth: AuthConnector = mockAuthConnector
    }
  }

  "MetadataController" should {
    "use the correct MetadataService" in {
      MetadataController.metadataService shouldBe MetadataService
    }
    "use the correct authconnector" in {
      MetadataController.auth shouldBe AuthConnector
    }
  }

  "createMetadata" should {
    "return a 201 when a new entry is created from the parsed json" in new Setup {
      MetadataServiceMocks.createMetadataRecord(Created(Json.toJson(validMetadata)))
      AuthenticationMocks.getCurrentAuthority(Some(validAuthority))

      val request = FakeRequest().withJsonBody(validMetadataJson)
      val result = call(controller.createMetadata, request)
      await(jsonBodyOf(result)).as[Metadata] shouldBe validMetadata
      status(result) shouldBe CREATED
    }

    "return a 403 - forbidden when the user is not authenticated" in new Setup {
      AuthenticationMocks.getCurrentAuthority(None)

      val request = FakeRequest().withJsonBody(validMetadataJson)
      val result = call(controller.createMetadata, request)
      status(result) shouldBe FORBIDDEN
    }
  }

  "searchMetadata" should {
    "return a 200 and a MetadataResponse as json if metadata is found" in new Setup {
      AuthenticationMocks.getCurrentAuthority(Some(validAuthority))
      MetadataServiceMocks.searchMetadataRecord("testOid", Ok(Json.toJson(validMetadataResponse)))

      val result = call(controller.searchMetadata, FakeRequest())
      await(jsonBodyOf(result)).as[MetadataResponse] shouldBe validMetadataResponse
      status(result) shouldBe OK
    }

    "return a 403 - forbidden when the user is not authenticated" in new Setup {
      AuthenticationMocks.getCurrentAuthority(None)

      val result = call(controller.searchMetadata, FakeRequest())
      status(result) shouldBe FORBIDDEN
    }
  }

  "retrieveMetadata" should {
    "return a 200 and a metadata model is one is found" in new Setup {
      MetadataServiceMocks.retrieveMetadataRecord("testRegId", Ok(Json.toJson(Some(validMetadataResponse))))
      AuthenticationMocks.getCurrentAuthority(Some(validAuthority))

      val result = call(controller.retrieveMetadata("12345"), FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)).asOpt[MetadataResponse] shouldBe Some(validMetadataResponse)
    }

    "return a 403 - forbidden when the user is not authenticated" in new Setup {
      AuthenticationMocks.getCurrentAuthority(None)

      val result = call(controller.retrieveMetadata("12345"), FakeRequest())
      status(result) shouldBe FORBIDDEN
    }
  }
}
