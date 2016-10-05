//package akinabot.verticle.di;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.inject.Injector;
//
//import io.vertx.core.Verticle;
//import io.vertx.core.impl.verticle.CompilingClassLoader;
//import io.vertx.core.spi.VerticleFactory;
//
//public class InjectorVerticleFactory implements VerticleFactory {
//	private final Logger log = LoggerFactory.getLogger(getClass());
//
//	private Injector injector;
//
//	public InjectorVerticleFactory(Injector injector) {
//		this.injector = injector;
//	}
//
//	@Override
//	public String prefix() {
//		return "java-inject";
//	}
//
//	@Override
//	public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
//		verticleName = VerticleFactory.removePrefix(verticleName);
//		
//		log.info("Deploying verticle: {}", verticleName);
//		Class<?> clazz;
//		if (verticleName.endsWith(".java")) {
//			CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, verticleName);
//			String className = compilingLoader.resolveMainClassName();
//			clazz = compilingLoader.loadClass(className);
//		} else {
//			clazz = classLoader.loadClass(verticleName);
//		}
//
//		return (Verticle) injector.getInstance(clazz);
//	}
//}
