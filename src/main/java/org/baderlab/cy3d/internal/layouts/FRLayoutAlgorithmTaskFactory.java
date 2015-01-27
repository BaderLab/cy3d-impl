package org.baderlab.cy3d.internal.layouts;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.command.CommandExecutorTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class FRLayoutAlgorithmTaskFactory implements TaskFactory {

	private final CommandExecutorTaskFactory commandTaskFactory;
	
	public FRLayoutAlgorithmTaskFactory(CommandExecutorTaskFactory taskFactory) {
		this.commandTaskFactory = taskFactory;
	}

	@Override
	public TaskIterator createTaskIterator() {
		Map<String,Object> args = new HashMap<>();
		args.put("layout3D", true);
		return commandTaskFactory.createTaskIterator("layout", "fruchterman-rheingold", args, null);
	}

	@Override
	public boolean isReady() {
		return true;
	}

}
