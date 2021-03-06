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

package repositories

import auth.AuthorisationResource
import models.Metadata
import play.api.Logger
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson._
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

trait MetadataRepository extends Repository[Metadata, BSONObjectID]{
  def createMetadata(metadata: Metadata): Future[Metadata]
  def searchMetadata(oid: String): Future[Option[Metadata]]
  def retrieveMetadata(regI: String): Future[Option[Metadata]]
  def oIDMetadataSelector(oID: String): BSONDocument
  def regIDMetadataSelector(registrationID: String): BSONDocument
  }

class MetadataMongoRepository(implicit mongo: () => DB)
  extends ReactiveRepository[Metadata, BSONObjectID](Collections.metadata, mongo, Metadata.formats, ReactiveMongoFormats.objectIdFormats)
  with MetadataRepository
  with AuthorisationResource[String] {

  override def ensureIndexes(implicit ec: ExecutionContext): Future[Seq[Boolean]] = Future.sequence(
    Seq(collection.indexesManager.ensure(Index(Seq("OID" -> IndexType.Ascending), name = Some("oidIndex"), unique = true)),
        collection.indexesManager.ensure(Index(Seq("registrationID" -> IndexType.Ascending), name = Some("regIDIndex"), unique = true))))

  override def oIDMetadataSelector(oID: String): BSONDocument = BSONDocument(
    "OID" -> BSONString(oID)
  )

  override def regIDMetadataSelector(registrationID: String): BSONDocument = BSONDocument(
    "registrationID" -> BSONString(registrationID)
  )

  override def createMetadata(metadata: Metadata): Future[Metadata] = {
    collection.insert(metadata).map { res =>
      if (res.hasErrors) {
        Logger.error(s"Failed to store metadata. Error: ${res.errmsg.getOrElse("")} for registration id ${metadata.registrationID}")
      }
      metadata
    }
  }

  override def retrieveMetadata(registrationID: String): Future[Option[Metadata]] = {
    val selector = regIDMetadataSelector(registrationID)
    collection.find(selector).one[Metadata]
  }

  def getOid(id: String) : Future[Option[(String,String)]] = {
  // TODO : this can be made more efficient by performing an index scan rather than document lookup
  retrieveMetadata(id) map {
      case None => None
      case Some(m) => Some((m.registrationID, m.OID))
    }
  }

  override def searchMetadata(oID: String): Future[Option[Metadata]] = {
    val selector = oIDMetadataSelector(oID)
    collection.find(selector).one[Metadata]
  }
}
