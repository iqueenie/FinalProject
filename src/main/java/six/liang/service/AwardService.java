package six.liang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import six.liang.model.Award;
import six.liang.model.AwardRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AwardService {

    @Autowired
    private AwardRepository awardRepository;

    public List<Award> findAllAwards() {
        return awardRepository.findAll();
    }

    public Optional<Award> findAwardById(Long id) {
        return awardRepository.findById(id);
    }

    @Transactional
    public Award createAward(Award award) {
        return awardRepository.save(award);
    }

    @Transactional
    public Optional<Award> updateAward(Long id, Award updatedAward) {
        return awardRepository.findById(id)
                .map(existingAward -> {
                    existingAward.setName(updatedAward.getName());
                    existingAward.setPoints(updatedAward.getPoints());
                    existingAward.setProbability(updatedAward.getProbability());
                    return awardRepository.save(existingAward);
                });
    }

    @Transactional
    public boolean deleteAward(Long id) {
        return awardRepository.findById(id)
                .map(award -> {
                    awardRepository.delete(award);
                    return true;
                })
                .orElse(false);
    }
}