//package com.waben.option.common.component;
//
//import com.netflix.client.config.IClientConfig;
//import com.netflix.config.DynamicDoubleProperty;
//import com.netflix.config.DynamicPropertyFactory;
//import com.netflix.loadbalancer.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import javax.annotation.Nullable;
//import java.util.Map;
//import java.util.Set;
//
//@Slf4j
//public class ZoneAvoidanceEnhancePredicate extends ZoneAvoidancePredicate {
//
//    private volatile DynamicDoubleProperty triggeringLoad = new DynamicDoubleProperty("ZoneAwareNIWSDiscoveryLoadBalancer.triggeringLoadPerServerThreshold", 0.2d);
//
//    private volatile DynamicDoubleProperty triggeringBlackoutPercentage = new DynamicDoubleProperty("ZoneAwareNIWSDiscoveryLoadBalancer.avoidZoneWithBlackoutPercetage", 0.99999d);
//
//    public ZoneAvoidanceEnhancePredicate(IRule rule, IClientConfig clientConfig) {
//        super(rule, clientConfig);
//        initDynamicProperties(clientConfig);
//    }
//
//    public ZoneAvoidanceEnhancePredicate(LoadBalancerStats lbStats, IClientConfig clientConfig) {
//        super(lbStats, clientConfig);
//        initDynamicProperties(clientConfig);
//    }
//
//    private void initDynamicProperties(IClientConfig clientConfig) {
//        if (clientConfig != null) {
//            triggeringLoad = DynamicPropertyFactory.getInstance().getDoubleProperty(
//                    "ZoneAwareNIWSDiscoveryLoadBalancer." + clientConfig.getClientName() + ".triggeringLoadPerServerThreshold", 0.2d);
//
//            triggeringBlackoutPercentage = DynamicPropertyFactory.getInstance().getDoubleProperty(
//                    "ZoneAwareNIWSDiscoveryLoadBalancer." + clientConfig.getClientName() + ".avoidZoneWithBlackoutPercetage", 0.99999d);
//        }
//    }
//
//    @Override
//    public boolean apply(@Nullable PredicateKey input) {
//        String serverZone = input.getServer().getZone();
//        if (serverZone == null) {
//            return verifyTargetServer(input);
//        }
//        LoadBalancerStats lbStats = getLBStats();
//        if (lbStats == null) {
//            return verifyTargetServer(input);
//        }
//        if (lbStats.getAvailableZones().size() <= 1) {
//            return verifyTargetServer(input);
//        }
//        Map<String, ZoneSnapshot> zoneSnapshot = ZoneAvoidanceEnhanceRule.createSnapshot(lbStats);
//        if (!zoneSnapshot.keySet().contains(serverZone)) {
//            return verifyTargetServer(input);
//        }
//        log.debug("Zone snapshots: {}", zoneSnapshot);
//        Set<String> availableZones = ZoneAvoidanceEnhanceRule.getAvailableZones(zoneSnapshot, triggeringLoad.get(), triggeringBlackoutPercentage.get());
//        log.debug("Available zones: {}", availableZones);
//        if(availableZones != null && availableZones.contains(input.getServer().getZone())) {
//            return verifyTargetServer(input);
//        }
//        return false;
//    }
//
//    private boolean verifyTargetServer(PredicateKey input) {
//        String targetServer = TargetServerContext.getTargetServer();
//        if (!StringUtils.isBlank(targetServer)) {
//            if(targetServer.equals(input.getServer().getHostPort())) {
//                return true;
//            }
//            return false;
//        }
//        return true;
//    }
//
//}
