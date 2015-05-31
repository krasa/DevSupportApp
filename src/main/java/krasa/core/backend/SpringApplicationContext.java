package krasa.core.backend;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;

	/**
	 * This method is called from within the ApplicationContext once it is done starting up, it will stick a reference
	 * to itself into this bean.
	 * 
	 * @param context
	 *            a reference to the ApplicationContext.
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		CONTEXT = context;
	}

	/**
	 * This is about the same as context.getBean("beanName"), except it has its own static handle to the Spring context,
	 * so calling this method statically will give access to the beans by name in the Spring application context. As in
	 * the context.getBean("beanName") call, the caller must cast to the appropriate target class. If the bean does not
	 * exist, then a Runtime error will be thrown.
	 * 
	 * @param beanName
	 *            the name of the bean to get.
	 * @return an Object reference to the named bean.
	 */
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return CONTEXT.getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return CONTEXT.getBean(requiredType);
	}

	public static Object getBean(String name, Object... args) throws BeansException {
		return CONTEXT.getBean(name, args);
	}

	public static Resource[] getResources(String locationPattern) throws IOException {
		return CONTEXT.getResources(locationPattern);
	}
}
