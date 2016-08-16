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

import fixtures.{MetadataFixture, MongoFixture}
import helpers.SCRSSpec
import models.{Metadata, MetadataResponse}
import play.api.test.Helpers._
import repositories.{MetadataRepository, Repositories}

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
    "create a new metadata document" in new Setup {
      MetadataRepositoryMocks.createMetadata(validMetadata)

      val result = service.createMetadataRecord(validMetadata)
      await(result) shouldBe validMetadataResponse
    }
  }

  "retrieveMetadataRecord" should {
    "return MetadataResponse when a metadata document is retrieved" in new Setup {
      MetadataRepositoryMocks.retrieveMetadata("testRegID", Some(validMetadata))

      val result = service.retrieveMetadataRecord("testRegID")
      await(result) shouldBe Some(validMetadataResponse)
    }

    "return None if no document is retrieved" in new Setup {
      MetadataRepositoryMocks.retrieveMetadata("testRegID", None)

      val result = service.retrieveMetadataRecord("testRegID")
      await(result) shouldBe None
    }
  }
  "searchMetadataRecord" should {
    "return MetadataResponse when a metadata document is retrieved" in new Setup {
      MetadataRepositoryMocks.searchMetadata("testOID", Some(validMetadata))

      val result = service.searchMetadataRecord("testOID")
      await(result) shouldBe Some(validMetadataResponse)
    }

    "return None if no document is retrieved" in new Setup {
      MetadataRepositoryMocks.searchMetadata("testOID", None)

      val result = service.searchMetadataRecord("testOID")
      await(result) shouldBe None
    }

  }
}
