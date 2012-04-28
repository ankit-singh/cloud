package cornell.cloud.dropsomething.co.db;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;



public class SimpleDBManager {

	public static final String csTable = "CLIENTSERVERTABLE";
	public static final String slTable = "SERVERLISTTABLE";
	public static final String sbTable = "SERVERBLOCKTABLE";


	/**
	 * The Amazon Simple DB object
	 */
	private AmazonSimpleDB simpleDB = null;
	/**
	 * The instance object
	 */
	private static SimpleDBManager instance  = null;

	public static SimpleDBManager getInstance(){
		if(instance == null){
			instance = new SimpleDBManager();
			instance.init();
		}
		return instance;
	}
	public void init(){
		if (simpleDB == null) {
			FileInputStream inputStream;
			try {
				String filePath = this.getClass()
						.getResource("/AwsCredentials.properties").getPath();
				inputStream = new FileInputStream(new File(filePath));
				simpleDB = new AmazonSimpleDBClient(new PropertiesCredentials(
						inputStream));
				createTables();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
			}
		}
	}

	private void createTables(){
		try {
			simpleDB.createDomain(new CreateDomainRequest(
					SimpleDBManager.csTable));
			simpleDB.createDomain(new CreateDomainRequest(
					SimpleDBManager.slTable));
			simpleDB.createDomain(new CreateDomainRequest(
					SimpleDBManager.sbTable));
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
	}
	public void addRecords(String tableName,HashMap<String, String> table){
		Set<String> keys = table.keySet();
		for(String key: keys){
			addRecord(tableName, key, table.get(key));
		}
	}
	public void addRecord(String tableName,String key,String value) {
		List<ReplaceableAttribute> dataList = new ArrayList<ReplaceableAttribute>();
		dataList.add(new ReplaceableAttribute("VAL", value, true));
		simpleDB.putAttributes(new PutAttributesRequest(tableName, key, dataList));
	}
	public String getRecord(String tableName,String key){
		GetAttributesRequest getRequest = new GetAttributesRequest().withDomainName(tableName).withItemName(key);
		GetAttributesResult replaceableAttribute = simpleDB.getAttributes(getRequest);            
		return replaceableAttribute.getAttributes().get(0).getValue();
	}
	public HashMap<String, String> getRecords(String tableName){     
		String selectExpression = "select * from `" + csTable
				+ "`";
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		HashMap<String, String> table = new HashMap<String, String>();
		for (Item item : simpleDB.select(selectRequest).getItems()) {
			table.put(item.getName(), item.getAttributes().get(0).getValue());
		}
		return table;
	}
	public static void main(String[] args){
	}

}
