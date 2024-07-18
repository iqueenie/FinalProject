package six.liang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import six.liang.model.Award;
import six.liang.model.LotteryRecord;
import six.liang.model.LotteryRecordRepository;
import six.hsiao.model.MembersBean;
import six.liang.model.AwardRepository;
import six.hsiao.model.MembersRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class LotteryRecordService {

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private LotteryRecordRepository lotteryRecordRepository;

    @Autowired
    private MembersRepository membersRepository;

    public boolean hasDrawnToday(String memberAccount) {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        LocalDate today = LocalDate.now();
        return lotteryRecordRepository.findByMemberBeanAndDrawDate(member, today).isPresent();
    }

    public Award drawLottery(String memberAccount) throws Exception {
        // 获取当前会员
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new Exception("Member not found.");
        }

        // 检查今天是否已经抽奖
        LocalDate today = LocalDate.now();
        Optional<LotteryRecord> todayRecords = lotteryRecordRepository.findByMemberBeanAndDrawDate(member, today);
        if (todayRecords.isPresent()) {
            throw new IllegalStateException("您今天已經參與過抽獎了，請明天再試。");
        }

        List<Award> awards = awardRepository.findAll();
        if (awards.isEmpty()) {
            throw new Exception("No awards available.");
        }

        // 抽奖逻辑
        Award award = getRandomAward(awards);
        member.setPoints(member.getPoints() + award.getPoints());
        membersRepository.save(member);

        // 记录抽奖
        LotteryRecord record = new LotteryRecord();
        record.setMemberBean(member);
        record.setMemberId(member.getMemberId());
        record.setDrawDate(today);
        lotteryRecordRepository.save(record);

        return award;
    }

    private Award getRandomAward(List<Award> awards) {
        Random random = new Random();
        double totalWeight = awards.stream().mapToDouble(Award::getProbability).sum();
        double randomValue = random.nextDouble() * totalWeight;

        for (Award award : awards) {
            randomValue -= award.getProbability();
            if (randomValue <= 0) {
                return award;
            }
        }
        return awards.get(awards.size() - 1); // fallback to the last award
    }

    public List<LotteryRecord> getRecordsByMemberAccount(String memberAccount) {
        MembersBean member = membersRepository.findByMemberAccount(memberAccount);
        if (member == null) {
            throw new RuntimeException("Member not found.");
        }
        return lotteryRecordRepository.findByMemberBean(member);
    }
}
