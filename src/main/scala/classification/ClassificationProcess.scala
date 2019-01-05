/**
  * Created by Hussam on 03.01.19.
  */
package classification

import org.apache.spark.ml.{Model, Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._



class Classification extends InitSpark {
  private var crossValidatorModel:CrossValidatorModel=null
  private var evaluator: BinaryClassificationEvaluator=null
  private var trainingData:Dataset[Employee]=null
  private var testData:Dataset[Employee]=null


  def train():String={
    import sqlContext.implicits._

    // define the scheme-structure of the data in the csv-file
    // value "true" sets it as nullable
    val employeeSchema=StructType(Array(
      StructField("age",IntegerType,true),
      StructField("attrition",StringType,true),
      StructField("businessTravel",StringType,true),
      StructField("dailyRate",IntegerType,true),
      StructField("department",StringType,true),
      StructField("distanceFromHome",IntegerType,true),
      StructField("education",IntegerType,true),
      StructField("educationField",StringType,true),
      StructField("employeeCount",IntegerType,true),
      StructField("employeeNumber",IntegerType,true),
      StructField("environmentSatisfaction",IntegerType,true),
      StructField("gender",StringType,true),
      StructField("hourlyRate",IntegerType,true),
      StructField("jobInvolvement",IntegerType,true),
      StructField("jobLevel",IntegerType,true),
      StructField("jobRole",StringType,true),
      StructField("jobSatisfaction",IntegerType,true),
      StructField("martialStatus",StringType,true),
      StructField("monthlyIncome",IntegerType,true),
      StructField("monthlyRate",IntegerType,true),
      StructField("numCompaniesWorkedIn",IntegerType,true),
      StructField("overTime",StringType,true),
      StructField("percentSalaryHike",IntegerType,true),
      StructField("performanceRating",IntegerType,true),
      StructField("relationshipSatisfaction",IntegerType,true),
      StructField("stockOptionLevel",IntegerType,true),
      StructField("totalWorkingYears",IntegerType,true),
      StructField("trainingTimesLast",IntegerType,true),
      StructField("workLifeBalance",IntegerType,true),
      StructField("yearsAtCompany",IntegerType,true),
      StructField("yearsInCurrentRole",IntegerType,true),
      StructField("yearsSinceLastPromotion",IntegerType,true),
      StructField("yearsWithCurManager",IntegerType,true)
    ))

    // path of the csv-file
    val csvFilePath="Dataset_for_Classification.csv"

    // read the csv-file with the datasets and ignore white space to avoid error while parsing integer value of a string
    // as the csv-file contains header with the names of the columns, the respective option should be set
    // the dataset will have the data type of the case class Employee
    val myData:Dataset[Employee]=spark.read.option("inferSchema","true")
      .option("header",true)
      .option("ignoreLeadingWhiteSpace",false)
      .option("ignoreTrailingWhiteSpace",false)
      .schema(employeeSchema)
      .csv(csvFilePath)
      .as[Employee]

    // cache the data to avoid unnecessary reading
    myData.cache
    //myData.printSchema()
    //myData.show(10)
    //myData.describe().show()

    // names of the feature columns
    // note: columns which have to be indexed as categories have the suffix "Index".
    val myFeatures:Array[String]=Array("age", "businessTravelIndex",
      "dailyRate", "departmentIndex","distanceFromHome",
      "education","educationFieldIndex", "employeeCount",
      "employeeNumber","environmentSatisfaction",
      "genderIndex","hourlyRate","jobInvolvement",
      "jobLevel", "jobRoleIndex","jobSatisfaction",
      "martialStatusIndex","monthlyIncome","monthlyRate",
      "numCompaniesWorkedIn","overTimeIndex",
      "percentSalaryHike","performanceRating",
      "relationshipSatisfaction","stockOptionLevel",
      "totalWorkingYears","trainingTimesLast",
      "workLifeBalance","yearsAtCompany",
      "yearsInCurrentRole", "yearsSinceLastPromotion",
      "yearsWithCurManager")



    // transform categorical string values to one-hot values for processing
    val businessTravelIndexer=new StringIndexer()
      .setInputCol("businessTravel")
      .setOutputCol("businessTravelIndex")

    val departmentIndexer=new StringIndexer()
      .setInputCol("department")
      .setOutputCol("departmentIndex")

    val educationFieldIndexer=new StringIndexer()
      .setInputCol("educationField")
      .setOutputCol("educationFieldIndex")

    val genderIndexer=new StringIndexer()
      .setInputCol("gender")
      .setOutputCol("genderIndex")

    val jobRoleIndexer=new StringIndexer()
      .setInputCol("jobRole")
      .setOutputCol("jobRoleIndex")

    val martialStatusIndexer=new StringIndexer()
      .setInputCol("martialStatus")
      .setOutputCol("martialStatusIndex")


    val overTimeIndexer=new StringIndexer()
      .setInputCol("overTime")
      .setOutputCol("overTimeIndex")

    // index also the label -> by that even multi-class prediction is supported
    val labelindexer = new StringIndexer()
      .setInputCol("attrition")
      .setOutputCol("label")

    val assembler = new VectorAssembler()
      .setInputCols(myFeatures)
      .setOutputCol("features")

    // Split the data into training and test sets (30% held out for testing).
     val Array(trainingData1, testData2) = myData.randomSplit(Array(0.7, 0.3))
    trainingData=trainingData1
    testData=testData2

    // Random Forest prediction
    /*val rf = new RandomForestClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setNumTrees(10)*/

    // set up a DecisionTreeClassifier estimator
    val decisionTree = new DecisionTreeClassifier().setLabelCol("label")
      .setFeaturesCol("features")

    // Chain indexers and tree-classifier in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(businessTravelIndexer,departmentIndexer,educationFieldIndexer,genderIndexer,overTimeIndexer,jobRoleIndexer,martialStatusIndexer,labelindexer, assembler,decisionTree))


    //  set decision tree's maxDepth parameter values to get optimal model
    val paramGrid = new ParamGridBuilder().addGrid(decisionTree.maxDepth,
      Array(2, 3, 4, 5, 6, 7)).build()

    // set Binary Classifier as evaluator
    evaluator = new BinaryClassificationEvaluator()
      .setLabelCol("label")
      .setRawPredictionCol("prediction")

    // using 5-fold cross validation
    // set the pipline as estimator
    // set the binary-classifier as evaluator
    val crossValidator = new CrossValidator().setEstimator(pipeline)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid).setNumFolds(5)

    /************** get model using training data *************/

    // fit training data with cross validator and get model
     crossValidatorModel = crossValidator.fit(trainingData)


  "training finished"
  }

  def predictTestData(): String ={
    import spark.implicits._
    // get best model after evaluat ing different tree-depth of the tree-classifier
    val bestModel = crossValidatorModel.bestModel

    // stage 9 is the model-processing in the pipeline as a decision tree
    val treeModel = bestModel.asInstanceOf[org.apache.spark.ml.PipelineModel]
      .stages(9).asInstanceOf[DecisionTreeClassificationModel]
    // println("tree model: " +"\n" + treeModel.toDebugString)



    /************** evaluate model using test data *************/
    val predictions = crossValidatorModel.transform(testData)

    val accuracy = evaluator.evaluate(predictions)
    println(s"accuracy=$accuracy")
    evaluator.explainParams()
    //print label, predictions and probabitlity
    val result = predictions.select("label", "prediction", "probability")
    result.show
    val myseq=result.collectAsList().get(0).toSeq
    println("myseq="+myseq)
    val labelPredictionDataset = predictions.select("label", "prediction")
    val totalCount = predictions.count()
    val correctPrediction = labelPredictionDataset.filter($"label" === $"prediction").count()
    val wrongPrediction = labelPredictionDataset.filter(not($"label" === $"prediction")).count()
    val correctPercentage = correctPrediction.toDouble / totalCount.toDouble


    val report=new StringBuilder().append("Total number of data sets: "+totalCount)
                      .append("\n")
                      .append("Correct predictions: "+correctPrediction)
          .append("\n")
          .append("Wrong predictions: "+wrongPrediction)
        .append("\n")
        .append("Acuracy: "+((correctPercentage*100).round/100.toDouble)*100)
        .append("\n")
        .toString()


    println("result.type"+labelPredictionDataset.getClass.toString)
    println("result="+labelPredictionDataset)
println("report:"+report)

    report


}
  def predictNewData(data:Employee):Seq[Any]={
    import spark.implicits._
    val bestModel = crossValidatorModel.bestModel

    // cast data set to a dataframe-object to be processed by spark
    val newDataframe=Seq(data).toDS()

    // predict new data set with the most optimal model
    val predictions = bestModel.transform(newDataframe)

    val result = predictions.select("prediction", "probability")

    // return prediction and probability as array with both elements as double type
    // as there is only one prediction because of a single data set, the first item is selected
    println("result.collectAsList()"+result.collectAsList())

    println("result.collectAsList().get(0)"+result.collectAsList().get(0))
    println("result.collectAsList().get(0)"+result.collectAsList().get(0))
    println("(result.collectAsList().get(0))(0)"+(result.collectAsList().get(0))(0))
    println("result.collectAsList().get(0).toseq"+result.collectAsList().get(0).toSeq)
    println("(result.collectAsList().get(0))(0).tostring"+(result.collectAsList().get(0))(0).toString)
    result.collectAsList().get(0).toSeq


  }

}