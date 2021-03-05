package com.johnsnowlabs.nlp.annotators

import com.johnsnowlabs.nlp.AnnotatorType.CHUNK
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.util.io.ResourceHelper
import com.johnsnowlabs.nlp.Annotation
import com.johnsnowlabs.tags.FastTest
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.{Dataset, Row}
import org.scalatest._

import scala.language.reflectiveCalls


trait EmailMatcherBehaviors { this: FlatSpec =>

  def testEmailMatcherStrategies(dataset: => Dataset[Row]): Unit = {

    it should "have the same emails extracted from file" taggedAs FastTest in {
      import ResourceHelper.spark.implicits._

      val expectedChunks = Array(
        Annotation(CHUNK, 23, 31, "the first", Map("sentence" -> "0", "chunk" -> "0", "identifier" -> "followed by 'the'")),
        Annotation(CHUNK, 71, 80, "ceremonies", Map("sentence" -> "1", "chunk" -> "0", "identifier" -> "ceremony"))
      )

      val documentAssembler = new DocumentAssembler().setInputCol("text").setOutputCol("document")//.setCleanupMode("shrink_full")

      val emailMatcher = new EmailMatcher()
        .setInputCols(Array("document"))
        .setOutputCol("emails")

      val pipeline = new Pipeline().setStages(Array(documentAssembler, emailMatcher))

      val resultsHtml = pipeline.fit(dataset).transform(dataset)

      val emailChunksHtml = resultsHtml.select("emails")
        .as[Seq[Annotation]]
        .collect.flatMap(_.toSeq)

      resultsHtml.show(10, false)

      for (annot <- emailChunksHtml) {
        println(annot.result)
      }
//      assert(regexChunks sameElements expectedChunks)
    }
  }
}
