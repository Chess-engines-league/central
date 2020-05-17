package net.purevirtual.chell.central.web.agent.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import net.purevirtual.chell.central.web.crud.entity.enums.GamePhase;
import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;


public class HybridSelect {

    public static BoardMove selectMove(HybridType type, List<BoardMove> moves, List<HybridAgent.HybridSubAgent> subagents) {
        String allOptions = moves.stream().map(m -> '"' + m.getMove() + '"').collect(Collectors.joining(", ", "[", "]"));
        switch (type) {
            case RANDOM:
                int choice = random().nextInt(moves.size());
                return make(moves.get(choice).getMove(), "Selected move by random from: " + allOptions);
            case VOTE:
                Map<String, List<BoardMove>> moveCount = moves.stream().collect(Collectors.groupingBy(t -> t.getMove()));
                Map<String, Integer> votes = new HashMap<>();
                moveCount.forEach((move, list) -> votes.put(move, list.size()));
                return elect(votes, allOptions);
            case RANDOM_ELO:
                List<Integer> weights = subagents.stream().map(s -> s.engineConfig.getElo()).collect(Collectors.toList());
                return make(randomWeight(moves, weights), "Selected move by random from: " + allOptions);
            case VOTE_ELO:
                Map<String, Integer> weightedVotes = new HashMap<>();
                for (int i = 0; i < moves.size(); i++) {
                    weightedVotes.merge(moves.get(i).getMove(), subagents.get(i).engineConfig.getElo(), (a, b) -> a + b);
                }
                return elect(weightedVotes, allOptions);
            default:
                throw new RuntimeException("HybridType " + type + " not supproted");
        }
    }
    
    private static Random random() {
        return ThreadLocalRandom.current();
    }
    
    private static BoardMove elect(Map<String, Integer> weightedVotes, String allOptions) {
        if (weightedVotes.size() == 1) {
            return make(weightedVotes.keySet().iterator().next(), "Selected move by unanimous vote");
        }
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> Long.compare(weightedVotes.get(a), weightedVotes.get(b)));
        pq.addAll(weightedVotes.keySet());
        String p1 = pq.poll();
        List<String> tied = new ArrayList<>();
        while(!pq.isEmpty()) {
            String p2 = pq.poll();
            long w1 = weightedVotes.get(p1);
            long w2 = weightedVotes.get(p2);
            if(w1 == w2) {
                tied.add(p2);
            } else {
                break;
            }
        }
        if (tied.isEmpty()) {
            return make(p1, "Selected move by vote from " + allOptions);
        } else {
            tied.add(p1);
            int choice = random().nextInt(tied.size());
            return make(tied.get(choice), "Selected move by vote with random tie-break from " + allOptions);
        }
        
    }
    
    private static BoardMove make(BoardMove move, String comment) {
        BoardMove bm = new BoardMove();
        bm.setMove(move.getMove());
        bm.setMove(move.getPonder());
        bm.setComment(comment);
        return bm;
    }
    
    private static BoardMove make(String move, String comment) {
        BoardMove bm = new BoardMove();
        bm.setMove(move);
        bm.setComment(comment);
        return bm;
    }
    
    public static String randomWeight(Map<String, Integer> weightedVotes) {
        List<String> moves = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        weightedVotes.forEach((move,weight)-> {
            moves.add(move);
            weights.add(weight);
        });
        return randomWeight(moves, weights);
    }
 
    public static <T> T randomWeight(List<T> items, List<Integer> weights) {
        long completeWeight = 0L;
        for (Integer weight : weights) {
            completeWeight += weight;
        }
        long r = (long) (random().nextDouble() * completeWeight);
        long countWeight = 0;
        for (int i = 0; i < items.size(); i++) {
            countWeight += weights.get(i);
            if (countWeight >= r)
                return items.get(i);
        }
        throw new ArrayIndexOutOfBoundsException("Should never happen, countWeight=" + countWeight + " vs completeWeight=" + completeWeight);
    }

}
