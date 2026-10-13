package me.almana.innovativecontraptions.content.energy.net;

import java.util.Map;

public final class EuFlow {

    public static void accumulatePath(long sinkCable, int delivered,
                                      Map<Long, Double> lossTo, Map<Long, Long> prev,
                                      Map<Long, Integer> out) {
        double lossAtSink = lossTo.get(sinkCable);
        Long node = sinkCable;
        while (node != null) {
            int eu = delivered + (int) Math.round(lossAtSink - lossTo.get(node));
            out.merge(node, eu, Integer::sum);
            node = prev.get(node);
        }
    }

    private EuFlow() {
    }
}
