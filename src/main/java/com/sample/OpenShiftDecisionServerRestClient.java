package com.sample;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
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
	public static final String KIESESSION = "ksession7";

	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

	public static void invokeRulesService() {
		LineItem lineItem = newLineItem("1", "xyz", "NH", "1");
		WorkItem workItem = newWorkItem("1", "abc", null, asList(lineItem), "NH");
		Rph rph1 = newRph("1", "NH", "xyz_c", "1");
		Rph rph2 = newRph("2", "NH", "abc", "1");

		Store store1 = newStore("1", "NH", asList(rph1, rph2), false, 10);

		KieServicesClient kieServicesClient = configure_new(SERVER_URL, USERNAME, PASSWORD);
		RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
		KieCommands kieCommands = KieServices.Factory.get().getCommands();

		List<Command<?>> commands = new ArrayList<Command<?>>();
		commands.add(kieCommands.newInsert(workItem, "workItem"));
		commands.add(kieCommands.newInsert(store1, "store1"));
		commands.add(kieCommands.newAgendaGroupSetFocus("assignment"));
		commands.add(kieCommands.newAgendaGroupSetFocus("store-qualification"));
		commands.add(kieCommands.newAgendaGroupSetFocus("certification"));
		commands.add(kieCommands.newAgendaGroupSetFocus("work-item-qualification"));
		commands.add(kieCommands.newFireAllRules());
		commands.add(kieCommands.newGetObjects("workItem"));
		commands.add(kieCommands.newGetObjects("store1"));

		Command<?> batchCommand = kieCommands.newBatchExecution(commands, KIESESSION);
		ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults(CONTAINER,
				batchCommand);

		if (executeResponse.getType() == ResponseType.SUCCESS) {
			System.out.println("Commands executed with success! Response: ");
			ExecutionResults results = executeResponse.getResult();
			System.out.println(results.getValue("workItem"));
			System.out.println(results.getValue("store1"));
		} else {
			System.out.println("Error executing rules. Message: ");
			System.out.println(executeResponse.getMsg());
		}

	}

	public static KieServicesClient configure_new(String url, String username, String password) {
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		config.setMarshallingFormat(FORMAT);

		return KieServicesFactory.newKieServicesClient(config);
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
		Store store = new Store(id, state, pharmacists, shortlisted, availability);
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
