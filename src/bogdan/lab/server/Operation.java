package bogdan.lab.server;

import java.io.Serializable;

public class Operation implements Serializable {
	private String operation;
	private String argument;
	private String login;
	private String password;
	private byte[] object;
	private byte[] secondObject;

	public byte[] getSecondObject() {
		return secondObject;
	}

	public void setSecondObject(byte[] secondObject) {
		this.secondObject = secondObject;
	}

	public Operation(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public Operation(String operation, String argument, String login, String password) {
		this.operation = operation;
		this.argument = argument;
		this.login = login;
		this.password = password;
	}

	public Operation(String operation, byte[] object, String login, String password) {
		this.operation = operation;
		this.login = login;
		this.password = password;
		this.object = object;
	}

	public Operation(String operation, String argument, String login, String password, byte[] object) {
		this.operation = operation;
		this.argument = argument;
		this.login = login;
		this.password = password;
		this.object = object;
	}

	public String getOperation() {
		return operation;
	}

	public String getArgument() {
		return argument;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public byte[] getObject() {
		return object;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public void setObject(byte[] object) {
		this.object = object;
	}

	public String toString() {
		return getOperation() + " " + getArgument();
	}
}
