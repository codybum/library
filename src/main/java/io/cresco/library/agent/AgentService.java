package io.cresco.library.agent;


public interface AgentService {
	AgentState getAgentState();
	void msgIn(String id, String msg);
}