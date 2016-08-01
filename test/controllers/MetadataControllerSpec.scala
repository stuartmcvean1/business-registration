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

import fixtures.MetadataFixture
import helpers.SCRSSpec
import models.Metadata
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.MetadataService
import play.api.mvc.Results.{Ok, Created}
import play.api.test.Helpers._

class MetadataControllerSpec extends SCRSSpec with MetadataFixture{

  class Setup {
    val controller = new MetadataController {
      override val metadataService: MetadataService = mockMetadataService
    }
  }

  "MetadataController" should {
    "use the correct MetadataService" in {
      MetadataController.metadataService shouldBe MetadataService
    }
  }

  "createMetadata" should {
    "return a 201 when an existing metadata record does not exist and a new entry is created from the parsed json" in new Setup {
      MetadataServiceMocks.createMetadataRecord(Created(Json.toJson(validMetadata)))

      val request = FakeRequest().withJsonBody(validMetadataJson)
      val result = call(controller.createMetadata, request)
      await(jsonBodyOf(result)).as[Metadata] shouldBe validMetadata
      status(result) shouldBe CREATED
    }

    "return a 200 when an existing metadata record exists the entry is updated from the parsed json" in new Setup {
      MetadataServiceMocks.createMetadataRecord(Ok(Json.toJson(validMetadata)))

      val request = FakeRequest().withJsonBody(validMetadataJson)
      val result = call(controller.createMetadata, request)
      await(jsonBodyOf(result)).as[Metadata] shouldBe validMetadata
      status(result) shouldBe OK
    }
  }

  "retrieveMetadata" should {
    "return a 200 and a metadata model" in new Setup {
      MetadataServiceMocks.retrieveMetadataRecord("testOID", Ok(Json.toJson(Some(validMetadata))))

      val result = call(controller.retrieveMetadata, FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)).asOpt[Metadata] shouldBe Some(validMetadata)
    }
  }
}
