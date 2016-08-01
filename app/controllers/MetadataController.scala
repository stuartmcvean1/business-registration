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

import models.Metadata
import play.api.libs.json.JsValue
import play.api.mvc.Action
import services.MetadataService
import uk.gov.hmrc.play.microservice.controller.BaseController

object MetadataController extends MetadataController {
  val metadataService = MetadataService
}

trait MetadataController extends BaseController {

  val metadataService: MetadataService

  def createMetadata: Action[JsValue] = Action.async(parse.json) {
    implicit request =>
      withJsonBody[Metadata] {
        metadata => metadataService.createMetadataRecord(metadata)
      }
  }

  //todo: get oid from AuthContext
  def retrieveMetadata = Action.async {
    implicit request =>
      metadataService.retrieveMetadataRecord("oidGoesHere")
  }
}
