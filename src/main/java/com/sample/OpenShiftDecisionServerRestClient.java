package com.sample;

import static com.sample.AgendaGroup.ASSIGNMENT;
import static com.sample.AgendaGroup.CERTIFICATION;
import static com.sample.AgendaGroup.STORE_QUALIFICATION;
import static com.sample.AgendaGroup.WORK_ITEM_QUALIFICATION;
import static com.sample.Artifact.newArtifact;
import static com.sample.TestObjectCreator.asList;
import static com.sample.TestObjectCreator.newLineItem;
import static com.sample.TestObjectCreator.newRph;
import static com.sample.TestObjectCreator.newStore;
import static com.sample.TestObjectCreator.newWorkItem;
import static java.lang.System.currentTimeMillis;

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

	private static final String USERNAME = "kieserver";
	private static final String PASSWORD = "kieserver1!";
	private static final String SERVER_URL = "http://policyquote-app-sandip-desale-cognizant-com-bxms-infra.cloudapps.na.openshift.opentlc.com/kie-server/services/rest/server";
	private static final String CONTAINER = "policyquote";
	private static final String KIESESSION = "ksession6";
	private static KieServicesClient KIE_CLIENT = null;
	private static RuleServicesClient RULES_CLIENT = null;
	private static KieCommands KIE_COMMANDS = null;
	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

	static {
		KIE_CLIENT = configure_new(SERVER_URL, USERNAME, PASSWORD);
		RULES_CLIENT = KIE_CLIENT.getServicesClient(RuleServicesClient.class);
		KIE_COMMANDS = KieServices.Factory.get().getCommands();
	}

	@SuppressWarnings("unchecked")
	public static void invokeRulesService() {
		long startMillis = currentTimeMillis();
		LineItem lineItem = newLineItem("1", "xyz", "NH", "1");
		WorkItem workItem = newWorkItem("1", "abc", null, asList(lineItem), "NH");
		Rph rph1 = newRph("1", "NH", "xyz_c", "1");
		Rph rph2 = newRph("2", "NH", "abc", "1");
		Store store1 = newStore("1", "NH", asList(rph1, rph2), false, 10);
		writeToConsole("Facts being injected to remote drools service:", workItem, store1);

		// Work Item Qualification
		ExecutionResults results = createAndExecuteCommands(asList(newArtifact(workItem, "workItem")),
				WORK_ITEM_QUALIFICATION);
		List<Object> objectList = (List<Object>) results.getValue("workItem");
		workItem = (WorkItem) objectList.get(0);

		// Certification
		results = createAndExecuteCommands(asList(newArtifact(workItem, "workItem")), CERTIFICATION);
		objectList = (List<Object>) results.getValue("workItem");
		workItem = (WorkItem) objectList.get(0);

		// Store Qualification
		results = createAndExecuteCommands(asList(newArtifact(workItem, "workItem"), newArtifact(store1, "store1")),
				STORE_QUALIFICATION);
		objectList = (List<Object>) results.getValue("store1");
		workItem = (WorkItem) objectList.get(0);
		store1 = (Store) objectList.get(1);

		// Assignment
		results = createAndExecuteCommands(asList(newArtifact(workItem, "workItem"), newArtifact(store1, "store1")),
				ASSIGNMENT);
		objectList = (List<Object>) results.getValue("store1");
		store1 = (Store) objectList.get(0);
		writeToConsole("Results:", store1, "Time taken: " + (currentTimeMillis() - startMillis));
	}

	private static ExecutionResults createAndExecuteCommands(List<Artifact> artifacts, AgendaGroup agendaGroup) {
		List<Command<?>> commands = new ArrayList<Command<?>>();
		for (Artifact artifact : artifacts)
			commands.add(KIE_COMMANDS.newInsert(artifact.getFact(), artifact.getFactName()));
		commands.add(KIE_COMMANDS.newAgendaGroupSetFocus(agendaGroup.getValue()));
		commands.add(KIE_COMMANDS.newFireAllRules());
		for (Artifact artifact : artifacts)
			commands.add(KIE_COMMANDS.newGetObjects(artifact.getFactName()));
		Command<?> batchCommand = KIE_COMMANDS.newBatchExecution(commands, KIESESSION);
		ServiceResponse<ExecutionResults> executeResponse = RULES_CLIENT.executeCommandsWithResults(CONTAINER,
				batchCommand);
		ExecutionResults results = null;
		if (executeResponse.getType() == ResponseType.SUCCESS)
			results = executeResponse.getResult();
		else
			System.out.println("Error executing rules. Message: " + executeResponse.getMsg());
		return results;
	}

	private static KieServicesClient configure_new(String url, String username, String password) {
		KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
		config.setMarshallingFormat(FORMAT);
		return KieServicesFactory.newKieServicesClient(config);
	}

	private static void writeToConsole(Object... objects) {
		for (Object object : objects)
			System.out.println(object);
	}

}
