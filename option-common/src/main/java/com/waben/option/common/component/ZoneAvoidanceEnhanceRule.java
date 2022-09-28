//package com.waben.option.common.component;
//
//import com.netflix.client.config.IClientConfig;
//import com.netflix.loadbalancer.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ZoneAvoidanceEnhanceRule extends ZoneAvoidanceRule {
//
//    private CompositePredicate compositePredicate;
//
//    public ZoneAvoidanceEnhanceRule() {
//        super();
//        ZoneAvoidanceEnhancePredicate zonePredicate = new ZoneAvoidanceEnhancePredicate(this, null);
//        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, null);
//        compositePredicate = createCompositePredicate(zonePredicate, availabilityPredicate);
//    }
//
//    private CompositePredicate createCompositePredicate(ZoneAvoidanceEnhancePredicate p1, AvailabilityPredicate p2) {
//        return CompositePredicate.withPredicates(p1, p2)
//                .addFallbackPredicate(p2)
//                .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
//                .build();
//    }
//
//    @Override
//    public void initWithNiwsConfig(IClientConfig clientConfig) {
//        ZoneAvoidanceEnhancePredicate zonePredicate = new ZoneAvoidanceEnhancePredicate(this, clientConfig);
//        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, clientConfig);
//        compositePredicate = createCompositePredicate(zonePredicate, availabilityPredicate);
//    }
//
//    static Map<String, ZoneSnapshot> createSnapshot(LoadBalancerStats lbStats) {
//        Map<String, ZoneSnapshot> map = new HashMap<String, ZoneSnapshot>();
//        for (String zone : lbStats.getAvailableZones()) {
//            ZoneSnapshot snapshot = lbStats.getZoneSnapshot(zone);
//            map.put(zone, snapshot);
//        }
//        return map;
//    }
//
//    @Override
//    public AbstractServerPredicate getPredicate() {
//        return compositePredicate;
//    }
//
//}
