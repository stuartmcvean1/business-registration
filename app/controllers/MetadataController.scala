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

import auth._
import connectors.AuthConnector
import models.{Metadata, MetadataRequest}
import play.api.libs.json.JsValue
import play.api.mvc.Action
import play.api.Logger
import services.MetadataService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

object MetadataController extends MetadataController {
  val metadataService = MetadataService
  val resourceConn = MetadataService.metadataRepository
  val auth = AuthConnector
}

trait MetadataController extends BaseController with Authenticated with Authorisation[String] {

  val metadataService: MetadataService

  def createMetadata: Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      authenticated {
        case NotLoggedIn => Future.successful(Forbidden)
        case LoggedIn(context) =>
          withJsonBody[MetadataRequest] {
            metadata => metadataService.createMetadataRecord(Metadata.empty.copy(
              OID = context.oid,
              language = metadata.language))
          }
        }
  }

  def searchMetadata = Action.async {
    implicit request =>
      authenticated {
        case NotLoggedIn => Future.successful(Forbidden)
        case LoggedIn(context) => metadataService.searchMetadataRecord(context.oid)
      }
  }

  def retrieveMetadata(registrationID: String) = Action.async {
    implicit request =>
      authorised(registrationID) {
        case Authorised(_) => metadataService.retrieveMetadataRecord(registrationID)
        case NotLoggedInOrAuthorised => {
          Logger.info(s"[MetadataController] [retrieveMetadata] User not logged in")
          Future.successful(Forbidden)
        }
        case NotAuthorised(_) => {
          Logger.info(s"[MetadataController] [retrieveMetadata] User logged in but not authorised for resource $registrationID")
          Future.successful(Forbidden)
        }
        case AuthResourceNotFound(_) => Future.successful(NotFound)
      }
  }
}
