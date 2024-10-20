package com.gexu.keycloak.bizservice.datasource;

import java.util.Map;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.gexu.keycloak.bizkeycloakmodel.repository",
    entityManagerFactoryRef = "keycloakEntityManager",
    transactionManagerRef = "keycloakTransactionManager")
public class Keycloak {

  private final Environment environment;

  @Autowired
  public Keycloak(Environment environment) {
    this.environment = environment;
  }

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.keycloak")
  public DataSource keycloakDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean keycloakEntityManager() {

    LocalContainerEntityManagerFactoryBean em
        = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(keycloakDataSource());
    em.setPackagesToScan("com.gexu.keycloak.bizkeycloakmodel.model");
    em.setJpaVendorAdapter(keycloakHibernateJpaVendorAdapter());
    em.setJpaPropertyMap(Map.of(
        "hibernate.hbm2ddl.auto", "none",
        "hibernate.show_sql", "false"
    ));
    return em;
  }

  @Bean
  public HibernateJpaVendorAdapter keycloakHibernateJpaVendorAdapter() {
    final var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabasePlatform(
        environment.getProperty("app.datasource.keycloak.dialect"));
    return hibernateJpaVendorAdapter;
  }


  @Bean
  public JpaTransactionManager keycloakTransactionManager() {

    JpaTransactionManager transactionManager
        = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(
        keycloakEntityManager().getObject());
    return transactionManager;
  }

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.biz")
  public DataSourceProperties bizDataSourceProperties() {

    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "app.datasource.biz.hikari")
  public HikariDataSource bizDataSource() {

    return bizDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @Primary
  public HibernateJpaVendorAdapter bizHibernateJpaVendorAdapter() {

    final var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabasePlatform(
        environment.getProperty("app.datasource.biz.dialect"));
    return hibernateJpaVendorAdapter;
  }
}
