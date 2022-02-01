package kup.get.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "energyEntityManagerFactory",
        transactionManagerRef = "energyTransactionManager",
        basePackages = {"kup.get.repository.energy"})
public class DatabaseEnergyConfig {

    @Primary
    @Bean(name = "energyDataSourceProperties")
    @ConfigurationProperties("spring.datasource-energy")
    public DataSourceProperties energyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "energyDataSource")
    @ConfigurationProperties("spring.datasource-energy.configuration")
    public DataSource energyDataSource(@Qualifier("energyDataSourceProperties") DataSourceProperties energyDataSourceProperties) {
        return energyDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "energyEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean energyEntityManagerFactory(
            EntityManagerFactoryBuilder energyEntityManagerFactoryBuilder, @Qualifier("energyDataSource") DataSource energyDataSource) {

        Map<String, String> energyJpaProperties = new HashMap<>();
        energyJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        energyJpaProperties.put("hibernate.hbm2ddl.auto", "update");

        return energyEntityManagerFactoryBuilder
                .dataSource(energyDataSource)
                .packages("kup.get.entity.energy")
                .persistenceUnit("energyDataSource")
                .properties(energyJpaProperties)
                .build();
    }

    @Primary
    @Bean(name = "energyTransactionManager")
    public PlatformTransactionManager energyTransactionManager(
            @Qualifier("energyEntityManagerFactory") EntityManagerFactory energyEntityManagerFactory) {
        return new JpaTransactionManager(energyEntityManagerFactory);
    }
}
