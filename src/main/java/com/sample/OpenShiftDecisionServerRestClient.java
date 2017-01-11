package com.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.commands.CallContainerCommand;
import org.kie.server.api.commands.CommandScript;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieServerCommand;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.api.model.ServiceResponsesList;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

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

	public static void invoke() {

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
		BatchExecutionCommand command = new BatchExecutionCommandImpl(commands, KIESESSION);

		// marshal object to xml
		String xStreamXml = BatchExecutionHelper.newXStreamMarshaller().toXML(command);

		// send the xml command via REST API to the Decision Server

		ServiceResponse<String> response = client.executeCommands(CONTAINER, xStreamXml);
		System.out.println(response.getResult());

		LineItem lineItem1 = new LineItem("1", "xyz", "NH", "1");

		List<LineItem> lineItem1List = new ArrayList<LineItem>();
		lineItemList.add(lineItem1);

		WorkItem workItem1 = new WorkItem("1", "abc", null, lineItem1List, "NH", false);
		Rph rph1 = new Rph("1", "NH", "xyz_c", "1", null, false);
		Rph rph2 = new Rph("2", "NH", "abc", "1", null, false);
		Rph rph3 = new Rph("3", "CO", "xyz_c", "2", null, false);
		Rph rph4 = new Rph("4", "CO", "abc", "2", null, false);
		Rph rph5 = new Rph("5", "CO", "ghs", "3", null, false);
		Rph rph6 = new Rph("6", "CO", "abc", "3", null, false);
		Rph rph7 = new Rph("7", "NH", "pog", "4", null, false);
		Rph rph8 = new Rph("8", "NH", "xyz_c", "4", null, false);

		List<Rph> rphLs1 = new ArrayList<Rph>();
		rphLs1.add(rph1);
		rphLs1.add(rph2);
		Store store1 = new Store("1", "NH", rphLs1, false, 10);

		List<Rph> rphLs2 = new ArrayList<Rph>();
		rphLs2.add(rph3);
		rphLs2.add(rph4);
		Store store2 = new Store("2", "CO", rphLs2, false, 10);

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

		// ******************************************************************************************************************************************************

		BatchExecutionCommand command1 = new BatchExecutionCommandImpl(invokeRulesService(), KIESESSION);

		// marshal object to xml
		String xStreamXml1 = BatchExecutionHelper.newXStreamMarshaller().toXML(command1);

		// send the xml command via REST API to the Decision Server

		ServiceResponse<String> response1 = client.executeCommands(CONTAINER, xStreamXml1);
		System.out.println(response1.getResult());

		// ***********************************************************************************************************************

		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(SERVER_URL, USERNAME, PASSWORD);
		config.setMarshallingFormat(MarshallingFormat.JAXB);
		KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(config);
		KieServerCommand call = new CallContainerCommand(CONTAINER, xStreamXml1);
		List<KieServerCommand> cmds = Arrays.asList(call);
		CommandScript script = new CommandScript(cmds);
		ServiceResponsesList serviceResponsesList = kieServicesClient.executeScript(script);
		for (ServiceResponse<? extends Object> serviceResponse : serviceResponsesList.getResponses())
			System.out.println(serviceResponse.getResult());

	}

	public static KieServicesClient configure(String url, String username, String password) {

		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		return KieServicesFactory.newKieServicesClient(config);

	}

	public static void invoke_new() {
		LineItem lineItem = new LineItem("1", "abc", "NH", "1");
		List<LineItem> lineItemList = new ArrayList<LineItem>();
		lineItemList.add(lineItem);
		WorkItem workItem = new WorkItem("1", "abc", null, lineItemList, "NH", false);
		// configure client
		KieServicesClient kieServicesClient = configure_new(SERVER_URL, USERNAME, PASSWORD);
		RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
		KieCommands commandsFactory = KieServices.Factory.get().getCommands();

		Command<?> insert = commandsFactory.newInsert(workItem);
		Command<?> fireAllRules = commandsFactory.newFireAllRules();
		InsertObjectCommand insertObjectCommand = new InsertObjectCommand(workItem, "testing-out-object");
		GetObjectsCommand getObjectsCommand = new GetObjectsCommand();
		getObjectsCommand.setOutIdentifier("testing-out-object");
		AgendaGroupSetFocusCommand agendaGroupSetFocusCommand = new AgendaGroupSetFocusCommand();
		agendaGroupSetFocusCommand.setName("work-item-qualification");
		Command<?> batchCommand = commandsFactory.newBatchExecution(
				Arrays.asList(insertObjectCommand, agendaGroupSetFocusCommand, getObjectsCommand, fireAllRules),
				KIESESSION);
		ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults(CONTAINER,
				batchCommand);
		if (executeResponse.getType() == ResponseType.SUCCESS) {
			System.out.println("Commands executed with success! Response: ");
			System.out.println(executeResponse.getResult().getValue("testing-out-object"));
		} else {
			System.out.println("Error executing rules. Message: ");
			System.out.println(executeResponse.getMsg());
		}

	}

	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

	public static KieServicesClient configure_new(String url, String username, String password) {
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		config.setMarshallingFormat(FORMAT);

		return KieServicesFactory.newKieServicesClient(config);
	}

	public static void invokeRulesService1() {
		LineItem lineItem = newLineItem("1", "xyz", "NH", "1");
		WorkItem workItem = newWorkItem("1", "abc", null, asList(lineItem), "NH");
		Rph rph1 = newRph("1", "NH", "xyz_c", "1");
		Rph rph2 = newRph("2", "NH", "abc", "1");
		Rph rph3 = newRph("3", "CO", "xyz_c", "2");
		Rph rph4 = newRph("4", "CO", "abc", "2");
		Rph rph5 = newRph("5", "CO", "ghs", "3");
		Rph rph6 = newRph("6", "CO", "abc", "3");
		Rph rph7 = newRph("7", "NH", "pog", "4");
		Rph rph8 = newRph("8", "NH", "xyz_c", "4");
		Store store1 = newStore("1", "NH", asList(rph1, rph2), false, 10);
		Store store2 = newStore("2", "CO", asList(rph3, rph4), false, 10);
		Store store3 = newStore("3", "CO", asList(rph5, rph6), false, 10);
		Store store4 = newStore("4", "NH", asList(rph7, rph8), false, 20);

		KieServicesClient kieServicesClient = configure_new(SERVER_URL, USERNAME, PASSWORD);
		RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
		KieCommands commandsFactory = KieServices.Factory.get().getCommands();

		Command<?> insert = commandsFactory.newInsert(workItem);
		Command<?> fireAllRules = commandsFactory.newFireAllRules();
		InsertObjectCommand workItemCommand = new InsertObjectCommand(workItem, "workItem");
		InsertObjectCommand store1Command = new InsertObjectCommand(store1, "store1Command");
		InsertObjectCommand store2Command = new InsertObjectCommand(store2, "store2Command");
		InsertObjectCommand store3Command = new InsertObjectCommand(store3, "store3Command");
		InsertObjectCommand store4Command = new InsertObjectCommand(store4, "store4Command");
		GetObjectsCommand getworkItemCommand = new GetObjectsCommand();
		getworkItemCommand.setOutIdentifier("testing-out-object");
		GetObjectsCommand getstore1Command = new GetObjectsCommand();
		getstore1Command.setOutIdentifier("store1Command");
		GetObjectsCommand getstore2Command = new GetObjectsCommand();
		getstore2Command.setOutIdentifier("store2Command");
		GetObjectsCommand getstore3Command = new GetObjectsCommand();
		getstore3Command.setOutIdentifier("store3Command");
		GetObjectsCommand getstore4Command = new GetObjectsCommand();
		getstore4Command.setOutIdentifier("store4Command");
		AgendaGroupSetFocusCommand agenda1 = new AgendaGroupSetFocusCommand();
		agenda1.setName("work-item-qualification");
		AgendaGroupSetFocusCommand agenda2 = new AgendaGroupSetFocusCommand();
		agenda2.setName("certification");
		AgendaGroupSetFocusCommand agenda3 = new AgendaGroupSetFocusCommand();
		agenda3.setName("store-qualification");
		AgendaGroupSetFocusCommand agenda4 = new AgendaGroupSetFocusCommand();
		agenda4.setName("assignment");
		Command<?> batchCommand = commandsFactory.newBatchExecution(
				Arrays.asList(workItemCommand, store1Command, store2Command, store3Command, store4Command, agenda1,
						fireAllRules, agenda2, fireAllRules, agenda3, fireAllRules, agenda4, getworkItemCommand,
						getstore1Command, getstore2Command, getstore3Command, getstore4Command, fireAllRules),
				KIESESSION);
		ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults(CONTAINER,
				batchCommand);
		if (executeResponse.getType() == ResponseType.SUCCESS) {
			System.out.println("Commands executed with success! Response: ");
			ExecutionResults results = executeResponse.getResult();
			System.out.println(results.getValue("testing-out-object"));
			System.out.println(results.getValue("store1Command"));
			System.out.println(results.getValue("store2Command"));
			System.out.println(results.getValue("store3Command"));
			System.out.println(results.getValue("store4Command"));

		} else {
			System.out.println("Error executing rules. Message: ");
			System.out.println(executeResponse.getMsg());
		}

	}

	private static List<GenericCommand<?>> invokeRulesService() {
		LineItem lineItem = newLineItem("1", "xyz", "NH", "1");
		WorkItem workItem = newWorkItem("1", "abc", null, asList(lineItem), "NH");
		Rph rph1 = newRph("1", "NH", "xyz_c", "1");
		Rph rph2 = newRph("2", "NH", "abc", "1");
		Rph rph3 = newRph("3", "CO", "xyz_c", "2");
		Rph rph4 = newRph("4", "CO", "abc", "2");
		Rph rph5 = newRph("5", "CO", "ghs", "3");
		Rph rph6 = newRph("6", "CO", "abc", "3");
		Rph rph7 = newRph("7", "NH", "pog", "4");
		Rph rph8 = newRph("8", "NH", "xyz_c", "4");
		Store store1 = newStore("1", "NH", asList(rph1, rph2), false, 10);
		Store store2 = newStore("2", "CO", asList(rph3, rph4), false, 10);
		Store store3 = newStore("3", "CO", asList(rph5, rph6), false, 10);
		Store store4 = newStore("4", "NH", asList(rph7, rph8), false, 20);
		List<GenericCommand<?>> commands = new ArrayList<GenericCommand<?>>();
		/*
		 * commands.add( (GenericCommand<?>)
		 * CommandFactory.newInsertElements(asList(store1, store2, store3,
		 * store4, workItem)));
		 */
		InsertObjectCommand insertObjectCommand = new InsertObjectCommand(workItem, "testing-out-object");
		commands.add(insertObjectCommand);
		AgendaGroupSetFocusCommand agendaGroupSetFocusCommand = new AgendaGroupSetFocusCommand();
		GetObjectsCommand getObjectsCommand = new GetObjectsCommand();
		getObjectsCommand.setOutIdentifier("testing-out-object");
		agendaGroupSetFocusCommand.setName("work-item-qualification");
		commands.add(agendaGroupSetFocusCommand);
		FireAllRulesCommand fireAllRulesCommand = new FireAllRulesCommand("RunAllRules");
		commands.add(fireAllRulesCommand);
		commands.add(getObjectsCommand);
		return commands;
	}

	protected static WorkItem newWorkItem(String id, String clientId, String status, List<LineItem> lineItems,
			String state) {
		WorkItem workItem = new WorkItem(id, clientId, status, lineItems, state, false);
		return workItem;
	}

	protected static LineItem newLineItem(String id, String name, String state, String orderId) {
		LineItem lineItem = new LineItem(id, name, state, orderId);
		return lineItem;
	}

	protected static Rph newRph(String id, String state, String certification, String pharmacyId) {
		Rph rph = new Rph(id, state, certification, pharmacyId, new ArrayList<WorkItem>(), false);
		return rph;
	}

	protected static Store newStore(String id, String state, List<Rph> pharmacists, boolean shortlisted,
			int availability) {
		Store store = new Store(id, state, pharmacists, false, availability);
		return store;
	}

	@SuppressWarnings("unchecked")
	protected static <T> List<T> asList(T... objects) {
		List<T> list = new ArrayList<T>();
		for (T object : objects)
			list.add(object);
		return list;
	}

}
