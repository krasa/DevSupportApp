package krasa.svn.backend.dto;

import java.io.Serializable;
import java.util.*;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResult implements Serializable {
	private List<MergeInfoResultItem> mergeInfoResultItems;

	public List<MergeInfoResultItem> getMergeInfoResultItems() {
		return mergeInfoResultItems;
	}

	public void setMergeInfoResultItems(List<MergeInfoResultItem> mergeInfoResultItems) {
		this.mergeInfoResultItems = mergeInfoResultItems;
	}

	public void add(MergeInfoResultItem mergeInfoResultItem) {
		if (mergeInfoResultItems == null) {
			mergeInfoResultItems = new ArrayList<>();
		}
		mergeInfoResultItems.add(mergeInfoResultItem);
	}

	public void addAll(List<MergeInfoResultItem> merges) {
		if (mergeInfoResultItems == null) {
			mergeInfoResultItems = new ArrayList<>();
		}
		mergeInfoResultItems.addAll(merges);
	}
}
