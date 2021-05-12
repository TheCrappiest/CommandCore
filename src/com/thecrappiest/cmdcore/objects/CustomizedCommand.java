package com.thecrappiest.cmdcore.objects;

import java.util.ArrayList;
import java.util.List;

public class CustomizedCommand {

	private String base = null;
	private List<String> actions = new ArrayList<>();
	private double cost = 0;
	private String invalidFunds = null;
	private String permission = null;
	private String noPermission = null;

	public CustomizedCommand(String commandBase, List<String> commandActions, double commandCost, String invalidFunds,
			String permNode, String noPerm) {
		base = commandBase;
		actions = commandActions;
		cost = commandCost;
		permission = permNode;
		noPermission = noPerm;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String cmd) {
		base = cmd;
	}

	public List<String> retrieveActions() {
		return actions;
	}

	public void addAction(String action) {
		actions.add(action);
	}

	public void addActions(List<String> actions) {
		this.actions.addAll(actions);
	}

	public void clearActions() {
		actions.clear();
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String retrieveInvalidFundsMessage() {
		return invalidFunds;
	}

	public void setInvalidFundsMessage(String message) {
		invalidFunds = message;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permNode) {
		permission = permNode;
	}

	public String getNoPermMessage() {
		return noPermission;
	}

	public void setNoPermMessage(String noPerm) {
		noPermission = noPerm;
	}

}
