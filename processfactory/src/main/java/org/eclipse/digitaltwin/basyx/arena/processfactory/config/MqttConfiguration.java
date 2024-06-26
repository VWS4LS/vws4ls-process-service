package org.eclipse.digitaltwin.basyx.arena.processfactory.config;

import org.eclipse.digitaltwin.basyx.arena.processfactory.handlers.KickstartMqttMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

@Configuration
@EnableConfigurationProperties({
        MqttSettings.class
})
public class MqttConfiguration {

    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttSettings settings) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(
                new String[] { String.format("tcp://%s:%s", settings.hostname(), settings.port()) });

        if (settings.username() != null && !settings.username().isEmpty()) {
            options.setUserName(settings.username());
        }

        if (settings.password() != null && !settings.password().isEmpty()) {
            options.setPassword(settings.password().toCharArray());
        }

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public IntegrationFlow mqttInbound(MqttSettings settings,
            MqttPahoClientFactory mqttClientFactory,
            KickstartMqttMessageHandler msgHandler) {
        return IntegrationFlow.from(
                new MqttPahoMessageDrivenChannelAdapter("mqtt-service", mqttClientFactory, settings.topic()))
                .handle(msgHandler)
                .get();
    }
}