package dimthread;

import dimthread.command.Commands;
import dimthread.command.ThreadCountCommand;
import dimthread.thread.IMutableMainThread;
import dimthread.thread.ThreadPool;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;

public class DimThread implements ModInitializer {

	public static final String MOD_ID = "dimthread";
	public static ThreadPool THREAD_POOL = new ThreadPool(3);

	@Override
	public void onInitialize() {
		Commands.INSTANCE.registerCommand(new ThreadCountCommand());
	}

	public static void swapThreadsAndRun(Runnable task, Object... threadedObjects) {
		Thread currentThread = Thread.currentThread();
		Thread[] oldThreads = new Thread[threadedObjects.length];

		for(int i = 0; i < oldThreads.length; i++) {
			oldThreads[i] = ((IMutableMainThread)threadedObjects[i]).getMainThread();
			((IMutableMainThread)threadedObjects[i]).setMainThread(currentThread);
		}

		task.run();

		for(int i = 0; i < oldThreads.length; i++) {
			((IMutableMainThread)threadedObjects[i]).setMainThread(oldThreads[i]);
		}
	}

	public static void attach(Thread thread, ServerWorld world) {
		attach(thread, world.getRegistryKey().getValue().getPath());
	}

	public static void attach(Thread thread, String name) {
		thread.setName(MOD_ID + "_" + name);
	}

	public static boolean owns(Thread thread) {
		return thread.getName().startsWith(MOD_ID);
	}

}
