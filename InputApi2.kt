import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.google.gson.Gson


class LambdaApi124: RequestHandler<SQSEvent, Void?> {
    fun DeleteItem(data: SQSData)
    {
        println("data recivbed $data")
        val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
//        val dynamoDB = DynamoDB(client)
        val request = DeleteItemRequest()
       request.tableName = "ApiDataTable123"
      request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
       request.setReturnValues(ReturnValue.ALL_OLD)
        /* Create a Map of Primary Key attributes */
        val keysMap: MutableMap<String, AttributeValue> = HashMap<String, AttributeValue>()
        keysMap["Phone"] = AttributeValue(data.Phone)
        keysMap["Name"] = AttributeValue().withS(data.Name)
        request.key = keysMap
//        val table= dynamoDB.getTable("ApiDataTable123")
//        val outcome  = table.deleteItem ("Phone",data.Phone)
        val res=client.deleteItem(request);
       println("deleted")
//        val deleteItemSpec = DeleteItemSpec()
//            .withPrimaryKey("Phone", data.Phone)
//            .withConditionExpression("#Name = :val")
//            .withNameMap(
//                NameMap()
//                    .with("#Name", "Name")
//            )
//            .withValueMap(
//                ValueMap()
//                    .withString(":val", data.Name)
//            )
//            .withReturnValues(ReturnValue.ALL_OLD)
//        val outcome = table.deleteItem(deleteItemSpec)

        println("Succesfully Deleted")
  }

    override fun handleRequest(event: SQSEvent, context: Context?): Void? {
        for (msg in event.records) {

            var message=msg.body.toString();
            val data = Gson().fromJson(message, SQSData::class.java)
            DeleteItem(data)
        }
        return null;
    }
}
data class SQSData (
    val Phone: String,
    val Name : String
        )



