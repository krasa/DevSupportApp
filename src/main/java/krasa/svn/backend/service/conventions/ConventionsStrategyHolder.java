package krasa.svn.backend.service.conventions;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConventionsStrategyHolder implements BeanFactoryAware {
	private static SvnConventionsStrategy strategy;

	@Value("${ConventionsStrategyHolder.strategyBeanName}")
	private String strategyBeanName;

	public static SvnConventionsStrategy getStrategy() {
		return strategy;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		strategy = (SvnConventionsStrategy) beanFactory.getBean(strategyBeanName);
	}
}
