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

import models.WhiteListDetailsSubmit
import play.api.libs.json.{JsValue, Json}
import services.UserRegisterService
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc.Action

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserRegisterController extends UserRegisterController {
  val userRegisterService = UserRegisterService
}

trait UserRegisterController extends BaseController {

  val userRegisterService : UserRegisterService

  def createUserRecord : Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[WhiteListDetailsSubmit] {
      userDetails =>
        userRegisterService.createRegistration(userDetails)
    }
  }

  def searchRegistrations(email : String)  = Action.async { implicit request =>
    userRegisterService.searchRegistrations(email)
  }

  def clearRecords() = Action.async { implicit request =>
    userRegisterService.dropUsers()
  }
}
