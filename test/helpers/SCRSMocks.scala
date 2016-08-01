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

package helpers

import models.Metadata
import org.mockito.Matchers
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.mvc.Result
import repositories.MetadataMongoRepository
import services.MetadataService

import scala.concurrent.Future

trait SCRSMocks {
  this: MockitoSugar =>

  lazy val mockMetadataService = mock[MetadataService]
  lazy val mockMetadataRepository = mock[MetadataMongoRepository]

  object MetadataServiceMocks {
    def createMetadataRecord(result: Result): OngoingStubbing[Future[Result]] = {
      when(mockMetadataService.createMetadataRecord(Matchers.any[Metadata]()))
        .thenReturn(Future.successful(result))
    }

    def retrieveMetadataRecord(oid: String, result: Result): OngoingStubbing[Future[Result]] = {
      when(mockMetadataService.retrieveMetadataRecord(Matchers.any()))
        .thenReturn(Future.successful(result))
    }
  }

  object MetadataRepositoryMocks {
    def createMetadata(metadata: Metadata): OngoingStubbing[Future[Metadata]] = {
      when(mockMetadataRepository.createMetadata(Matchers.any[Metadata]()))
        .thenReturn(Future.successful(metadata))
    }

    def retrieveMetadata(oID: String, metadata: Option[Metadata]): OngoingStubbing[Future[Option[Metadata]]] = {
      when(mockMetadataRepository.retrieveMetaData(Matchers.any()))
        .thenReturn(Future.successful(metadata))
    }
  }
}
