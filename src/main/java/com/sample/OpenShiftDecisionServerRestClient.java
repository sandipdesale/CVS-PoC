package com.sample;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import com.cognizant.cvs.poc.routing_rules.LineItem;
import com.cognizant.cvs.poc.routing_rules.Rph;
import com.cognizant.cvs.poc.routing_rules.Store;
import com.cognizant.cvs.poc.routing_rules.WorkItem;

public class OpenShiftDecisionServerRestClient {

	public static final String USERNAME = "kieserver";
	public static final String PASSWORD = "kieserver1!";
	public static final String SERVER_URL = "http://policyquote-app-sandip-desale-cognizant-com-bxms-infra.cloudapps.na.openshift.opentlc.com/kie-server/services/rest/server";
	public static final String CONTAINER = "policyquote";
	public static final String KIESESSION = "ksession6";

	public static void main(String args[]) {

		// configure client
		KieServicesClient client = configure(SERVER_URL, USERNAME, PASSWORD);

		LineItem lineItem = new LineItem("1", "xyz", "NH", "1");
		List<LineItem> lineItemList = new ArrayList<LineItem>();
		lineItemList.add(lineItem);
		WorkItem workItem = new WorkItem("1", "abc", null, lineItemList, "NH", false);

		
		// generate commands
		List<GenericCommand<?>> commands = new ArrayList<GenericCommand<?>>();
		commands.add((GenericCommand<?>) CommandFactory.newInsert(workItem, "insert-identifier"));
		commands.add((GenericCommand<?>) CommandFactory.newFireAllRules("fire-identifier"));
		BatchExecutionCommand command = new BatchExecutionCommandImpl(commands,KIESESSION);

		// marshal object to xml
		String xStreamXml = BatchExecutionHelper.newXStreamMarshaller().toXML(command);

		// send the xml command via REST API to the Decision Server
		
		ServiceResponse<String> response = client.executeCommands(CONTAINER, xStreamXml);
		System.out.println(response.getResult());

		
		LineItem lineItem1 = new LineItem("1", "xyz", "NH", "1");

		List<LineItem> lineItem1List = new ArrayList<LineItem>();
		lineItemList.add(lineItem1);
		
		WorkItem workItem1 = new WorkItem("1", "abc", null, lineItem1List, "NH",false);
		Rph rph1 = new Rph("1", "NH", "xyz_c", "1",null,false);
		Rph rph2 = new Rph("2", "NH", "abc", "1",null,false);
		Rph rph3 = new Rph("3", "CO", "xyz_c", "2",null,false);
		Rph rph4 = new Rph("4", "CO", "abc", "2",null,false);
		Rph rph5 = new Rph("5", "CO", "ghs", "3",null,false);
		Rph rph6 = new Rph("6", "CO", "abc", "3",null,false);
		Rph rph7 = new Rph("7", "NH", "pog", "4",null,false);
		Rph rph8 = new Rph("8", "NH", "xyz_c", "4",null,false);
		
		List<Rph> rphLs1 = new ArrayList<Rph>();
		rphLs1.add(rph1);
		rphLs1.add(rph2);
		Store store1 = new Store("1", "NH", rphLs1, false, 10);
		
		List<Rph> rphLs2 = new ArrayList<Rph>();
		rphLs2.add(rph3);
		rphLs2.add(rph4);
		Store store2 = new Store("2", "CO",rphLs2 , false, 10);
		
		
		List<Rph> rphLs3 = new ArrayList<Rph>();
		rphLs3.add(rph5);
		rphLs3.add(rph6);
		Store store3 = new Store("3", "CO", rphLs3, false, 10);
		
		List<Rph> rphLs4 = new ArrayList<Rph>();
		rphLs4.add(rph7);
		rphLs4.add(rph8);
		
		Store store4 = new Store("4", "NH", rphLs4, false, 20);


		// generate commands
		List<GenericCommand<?>> commands1 = new ArrayList<GenericCommand<?>>();
		
		List l = new ArrayList();
		l.add(workItem);
		l.add(store1);
		l.add(store2);
		l.add(store3);
		l.add(store4);
		
		commands1.add((GenericCommand<?>) CommandFactory.newInsertElements(l));
		
		commands1.add((GenericCommand<?>) CommandFactory.newFireAllRules("fire-identifier"));
		BatchExecutionCommand command1 = new BatchExecutionCommandImpl(commands1,KIESESSION);
		
		// marshal object to xml
		String xStreamXml1 = BatchExecutionHelper.newXStreamMarshaller().toXML(command1);

		// send the xml command via REST API to the Decision Server
		
		ServiceResponse<String> response1 = client.executeCommands(CONTAINER, xStreamXml1);
		System.out.println(response1.getResult());

		
		
		
	}

	public static KieServicesClient configure(String url, String username, String password) {

		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		return KieServicesFactory.newKieServicesClient(config);

	}

}
