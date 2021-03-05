package com.johnsnowlabs.nlp.annotators

import com.johnsnowlabs.nlp.AnnotatorType.{CHUNK, DOCUMENT}
import com.johnsnowlabs.nlp._
import org.apache.commons.validator.routines.{DomainValidator, EmailValidator}
import org.apache.spark.ml.util.Identifiable

import scala.util.matching.Regex


/**
  * Detect and clean emails in a text
  *
  * See [[https://github.com/JohnSnowLabs/spark-nlp/blob/master/src/test/scala/com/johnsnowlabs/nlp/annotators/EmailMatcherTestSpec.scala]] for example on how to use this API.
  *
  * @param uid internal element required for storing annotator to disk
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
  *
  */
class EmailMatcherModel(override val uid: String) extends AnnotatorModel[EmailMatcherModel] {

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

  def this() = this(Identifiable.randomUID("EMAIL_MATCHER"))

  def extractAndCleanEmail(potentialEmail: Regex.Match): (String, Int, Int) = {
    val emailRegex = """(\w[\w\.-]*@[\w\.-]+\.\w{2,3})""".r
    val email = emailRegex.findFirstMatchIn(potentialEmail.group(1)).orNull

    if (email == null) return null

    val startEmail = potentialEmail.start + email.start
    val endEmail = potentialEmail.start + email.end

    val splittedEmailByPoint = email.group(1).split("\\.")
    val splittedEmailByAt = email.group(1).split("@")

    val tld = splittedEmailByPoint.last
    val domain = splittedEmailByAt.last

    val emailValidator = EmailValidator.getInstance
    val domainValidator = DomainValidator.getInstance
    if (emailValidator.isValid(email.group(1)) == true) {
      return (email.group(1),
              startEmail,
              endEmail)
    }
    if (!domainValidator.isValid(domain)){
//        println("------------>" + domain)
    }
    if (!domainValidator.isValidTld(tld)){
      if (tld.size == 3){
        val newTld = tld.substring(0,2)
//          println("new tld : " + newTld)
        if (domainValidator.isValidTld(newTld)){
//            println("new tld valid : " + newTld)
          splittedEmailByPoint.update(splittedEmailByPoint.length - 1, newTld)
//            println("new email : " + splittedEmailByPoint.mkString("."))
          return (splittedEmailByPoint.mkString("."),
            startEmail,
            endEmail - 1)
        }
      }
    }
    return null
  }


  override def annotate(annotations: Seq[Annotation]): Seq[Annotation] = {
    var results = Seq[Annotation]()
    println(annotations)
    annotations.zipWithIndex.map { case (annotation, annotationIndex) =>

      val simpleEmailRegex = """([\w\.-]+@[\w\.-]+)""".r

      for (potentialEmail <-  simpleEmailRegex.findAllMatchIn(annotation.result)) {
        val emailCleaned = extractAndCleanEmail(potentialEmail)
        if (emailCleaned != null) {
          println("---------------")
          println(potentialEmail.toString())
          println(emailCleaned._1)
          val ann = Annotation(
            outputAnnotatorType,
            emailCleaned._2, // start index email
            emailCleaned._3, // end index email
            emailCleaned._1, // email
            null
          )
          results = results :+ ann
        }
      }
    }
  results
  }
}
