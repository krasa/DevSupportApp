package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;

/**
 * @author Vojtech Krasa
 */
public interface MergeInfoService {
	MergeInfoResult findMerges(List<SvnFolder> branches);
}
