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

package fixtures

import reactivemongo.api.{MongoConnection, FailoverStrategy, DB}
import uk.gov.hmrc.mongo.MongoConnector

trait MongoFixture {

  private lazy val mongoUri: String = s"mongodb://127.0.0.1:27017/scrs"
  private lazy val conn = new MongoConnector(mongoUri)

  lazy val mongoDB = () => new DB {
    override def failoverStrategy: FailoverStrategy = conn.helper.failoverStrategy.getOrElse(FailoverStrategy())
    override def connection: MongoConnection = conn.helper.connection
    override def name: String = conn.helper.dbName
  }

}
