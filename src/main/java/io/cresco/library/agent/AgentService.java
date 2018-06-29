package io.cresco.library.agent;


import io.cresco.library.messaging.MsgEvent;

public interface AgentService {
	AgentState getAgentState();
	void msgOut(String id, MsgEvent msg);
}