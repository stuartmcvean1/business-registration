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

import models.Metadata
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.{Ok, Created}
import repositories.{Repositories, MetadataRepository}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ExecutionContext, Future}

object MetadataService extends MetadataService {
  override val metadataRepository = Repositories.metadataRepository
}

trait MetadataService {

  val metadataRepository: MetadataRepository

  def createOrUpdateMetadata(metadata: Metadata): Future[Result] = {
    metadataExists(metadata.OID).flatMap {
      if (_) updateMetadataRecord(metadata) else createMetadataRecord(metadata)
    }
  }

  private[services] def updateMetadataRecord(metadata: Metadata)(implicit ex: ExecutionContext): Future[Result] = {
    metadataRepository.updateMetadata(metadata).map(res => Ok(Json.toJson(res)))
  }

  private[services] def metadataExists(OID: String)(implicit ex: ExecutionContext): Future[Boolean] = {
    metadataRepository.retrieveMetaData(OID).map{
      case Some(_) => true
      case _ => false
    }
  }

  private[services] def createMetadataRecord(metadata: Metadata)(implicit ex: ExecutionContext): Future[Result] = {
    metadataRepository.createMetadata(metadata).map(res => Created(Json.toJson(res)))
  }
}
