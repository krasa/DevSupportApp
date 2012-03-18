package krasa.backend.service;

import krasa.backend.domain.SvnFolder;
import krasa.backend.dto.MergeInfoResult;

import java.util.List;

/**
 * @author Vojtech Krasa
 */
public interface MergeInfoService {
    MergeInfoResult findMerges(List<SvnFolder> branches);
}
