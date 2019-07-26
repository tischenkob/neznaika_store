package bogdan.lab.server;

public class ServerUser {
	int port;
	Operation operation;

	ServerUser(int port, Operation operation){
		this.port = port;
		this.operation = operation;
	}

	public int getPort() {
		return port;
	}

	public Operation getOperation() {
		return operation;
	}
}
