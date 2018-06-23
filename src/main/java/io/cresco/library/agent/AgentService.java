package io.cresco.library.agent;


public interface AgentService {
	Agent getAgent();
	void msgIn(String msg);
}