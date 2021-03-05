package com.johnsnowlabs.nlp.annotators

import com.johnsnowlabs.nlp.AnnotatorApproach
import com.johnsnowlabs.nlp.AnnotatorType.{CHUNK, DOCUMENT}
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.util.{DefaultParamsReadable, Identifiable}
import org.apache.spark.sql.Dataset


/**
  * Matchs, extracts and cleans emails from text
  *
  * @param uid internal element required for storing annotator to disk
  *
  *    See [[https://github.com/JohnSnowLabs/spark-nlp/blob/master/src/test/scala/com/johnsnowlabs/nlp/annotators/EmailMatcherTestSpec.scala]] for example on how to use this API.
  * @groupname anno Annotator types
  * @groupdesc anno Required input and expected output annotator types
  * @groupname Ungrouped Members
  * @groupname param Parameters
  * @groupname setParam Parameter setters
  * @groupname getParam Parameter getters
  * @groupname Ungrouped Members
  * @groupprio param  1
  * @groupprio anno  2
  * @groupprio Ungrouped 3
  * @groupprio setParam  4
  * @groupprio getParam  5
  * @groupdesc Parameters A list of (hyper-)parameter keys this annotator can take. Users can set and get the parameter values through setters and getters, respectively.
  **/
class EmailMatcher(override val uid: String) extends AnnotatorApproach[EmailMatcherModel] {

  /** Match, extract and clean emails from text */
  override val description: String = "Matchs, extracts and cleans emails from text"

  /** Ouput annotator type: CHUNK
    *
    * @group anno
    **/
  override val outputAnnotatorType: AnnotatorType = CHUNK
  /** Input annotator type: DOCUMENT
    *
    * @group anno
    **/
  override val inputAnnotatorTypes: Array[AnnotatorType] = Array(DOCUMENT)

  setDefault(
    inputCols -> Array(DOCUMENT)
  )

  def this() = this(Identifiable.randomUID("EMAIL_MATCHER"))


  override def train(dataset: Dataset[_], recursivePipeline: Option[PipelineModel]): EmailMatcherModel = {
    new EmailMatcherModel()
  }

}

object EmailMatcher extends DefaultParamsReadable[EmailMatcher]