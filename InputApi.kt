import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.google.gson.Gson

import software.amazon.awssdk.services.sqs.model.SqsException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.charset.Charset


class LambdaApi123 : RequestStreamHandler {

    override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("US-ASCII")))
        val data = Gson().fromJson(reader, ResponseFromAPI::class.java)
        println(data)
        val res = Gson().fromJson(data.body, Dynamo::class.java)
         val client: AmazonDynamoDB =AmazonDynamoDBClientBuilder.defaultClient()
        val dynamoDB = DynamoDB(client)
        val table: Table = dynamoDB.getTable("ApiDataTable123")
        val item: Item = Item()
            .withPrimaryKey("Phone",res.Phone)
            .withString("Name", res.Name)

        val outcome: PutItemOutcome = table.putItem(item)
        println("Created Succesfully")


        val queue = "https://sqs.us-east-1.amazonaws.com/937370513820/DataApi123"
        val sqsClient = AmazonSQSClientBuilder.defaultClient()

        sendSingleMessage(sqsClient, queue,Gson().toJson(res))

    }
    private fun sendSingleMessage(sqsClient: AmazonSQS, queue: String,data:String) {
        try {
            val msg= SendMessageRequest()
            msg.queueUrl=queue
            msg.messageBody=data
            sqsClient.sendMessage(msg)
            println("Message has been sent successfully")
        } catch (e: SqsException) {
            System.err.println(e.awsErrorDetails().errorMessage())
            System.exit(1)
        }

    }

}
data class ResponseFromAPI(
    var body: String,
)
data class Dynamo(
    val Phone: String,
    val Name: String,

)


