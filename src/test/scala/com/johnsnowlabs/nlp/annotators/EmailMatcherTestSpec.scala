package com.johnsnowlabs.nlp.annotators

import com.johnsnowlabs.nlp.DataBuilder
import org.apache.spark.sql.{Dataset, Row}
import org.scalatest._

import scala.io.Source

class EmailMatcherTestSpec extends FlatSpec with EmailMatcherBehaviors {
  val dataTest = Source.fromFile("src/test/resources/email-matcher/test-all-cases.txt").getLines().mkString(" ")
//  val dataTest = Source.fromFile("src/test/resources/email-matcher/test.html").getLines().mkString(" ")
  val df: Dataset[Row] = DataBuilder.basicDataBuild(dataTest)
  "A full EmailMatcher pipeline with content" should behave like testEmailMatcherStrategies(df)
}
