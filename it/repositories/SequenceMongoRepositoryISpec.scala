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

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.mongo.MongoSpecSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SequenceMongoRepositoryISpec extends UnitSpec with MongoSpecSupport with BeforeAndAfterEach with ScalaFutures with Eventually with WithFakeApplication {

  val testSequence = "wibble"

  class Setup {
    val repository = new SequenceMongoRepository()
    await(repository.drop)
    await(repository.ensureIndexes)
  }

  "SequenceRepository" should {

    "get a sequence id" in new Setup {
      val response = await(repository.getNext(testSequence))
      response shouldBe (1)
    }

    "get sequences, one after another from 1 to the end" in new Setup {

      val inputs = 1 to 20
      val outputs = inputs map { _ => await(repository.getNext(testSequence)) }
      outputs shouldBe inputs
    }
  }
}