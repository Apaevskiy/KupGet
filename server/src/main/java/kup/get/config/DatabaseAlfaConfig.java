package kup.get.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "alfaEntityManagerFactory",
        transactionManagerRef = "alfaTransactionManager",
        basePackages = {"kup.get.repository.alfa"})
public class DatabaseAlfaConfig {

    @Bean(name = "alfaDataSourceProperties")
    @ConfigurationProperties("spring.datasource-alfa")
    public DataSourceProperties alfaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "alfaDataSource")
    @ConfigurationProperties("spring.datasource-alfa.configuration")
    public DataSource alfaDataSource(@Qualifier("alfaDataSourceProperties") DataSourceProperties alfaDataSourceProperties) {
        return alfaDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "alfaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean alfaEntityManagerFactory(
            EntityManagerFactoryBuilder alfaEntityManagerFactoryBuilder, @Qualifier("alfaDataSource") DataSource alfaDataSource) {

        Map<String, String> alfaJpaProperties = new HashMap<>();
        alfaJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.FirebirdDialect");

        return alfaEntityManagerFactoryBuilder
                .dataSource(alfaDataSource)
                .packages("kup.get.entity.alfa")
                .persistenceUnit("alfaDataSource")
                .properties(alfaJpaProperties)
                .build();
    }

    @Bean(name = "alfaTransactionManager")
    public PlatformTransactionManager alfaTransactionManager(
            @Qualifier("alfaEntityManagerFactory") EntityManagerFactory alfaEntityManagerFactory) {

        return new JpaTransactionManager(alfaEntityManagerFactory);
    }
}