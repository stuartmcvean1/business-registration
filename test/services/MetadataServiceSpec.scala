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

package services

import fixtures.{MongoFixture, MetadataFixture}
import helpers.SCRSSpec
import models.Metadata
import repositories.{Repositories, MetadataRepository}
import play.api.test.Helpers._

class MetadataServiceSpec extends SCRSSpec with MetadataFixture with MongoFixture{

  implicit val mongo = mongoDB

  class Setup {
    val service = new MetadataService {
      override val metadataRepository: MetadataRepository = mockMetadataRepository
    }
  }

  "MetdataService" should {
    "use the correct MetadataRepository" in {
      MetadataService.metadataRepository shouldBe Repositories.metadataRepository
    }
  }

  "createMetadataRecord" should {
    "create a new metadata record" in new Setup {
      MetadataRepositoryMocks.createMetadata(validMetadata)

      val result = service.createMetadataRecord(validMetadata)
      status(result) shouldBe CREATED
      await(jsonBodyOf(result)).as[Metadata] shouldBe validMetadata
    }
  }

  "retrieveMetadataRecord" should {
    "return metadata Json and a 200 when a metadata record is retrieved" in new Setup {
      MetadataRepositoryMocks.retrieveMetadata("testOID", Some(validMetadata))

      val result = service.retrieveMetadataRecord("testOID")
      status(result) shouldBe OK
      await(jsonBodyOf(result)).as[Metadata] shouldBe validMetadata
    }

    "return a 201 - Created when no record is retrieved so a new one is created" in new Setup {
      MetadataRepositoryMocks.retrieveMetadata("testOID", None)
      MetadataRepositoryMocks.createMetadata(validMetadata)

      val result = service.retrieveMetadataRecord("testOID")
      status(result) shouldBe CREATED
      await(jsonBodyOf(result)).asOpt shouldBe None
    }
  }
}
