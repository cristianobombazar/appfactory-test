/**
 * @author srinag.boda
 *
 */

package com.kony.sbg.dto;

import com.dbp.core.api.DBPDTO;

public class SbgArrangementsDTO implements DBPDTO {

	private static final long serialVersionUID = 1L;
	private String Account_id;
	private String arrangementId;
	private String extensionData;

	public SbgArrangementsDTO(String account_id, String arrangementId, String extensionData) {
		super();
		this.Account_id = account_id;
		this.arrangementId = arrangementId;
		this.extensionData = extensionData;
	}

	public String getAccount_id() {
		return Account_id;
	}

	public void setAccount_id(String Account_id) {
		this.Account_id = Account_id;
	}

	public String getarrangementId() {
		return arrangementId;
	}

	public void setarrangementId(String arrangementId) {
		this.arrangementId = arrangementId;
	}

	public String getextensionData() {
		return extensionData;
	}

	public void setextensionData(String extensionData) {
		this.extensionData = extensionData;
	}

	public SbgArrangementsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Account_id == null) ? 0 : Account_id.hashCode());
		result = prime * result + ((arrangementId == null) ? 0 : arrangementId.hashCode());
		result = prime * result + ((extensionData == null) ? 0 : extensionData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SbgArrangementsDTO other = (SbgArrangementsDTO) obj;
		if (Account_id == null) {
			if (other.Account_id != null)
				return false;
		} else if (!Account_id.equals(other.Account_id))
			return false;
		if (arrangementId == null) {
			if (other.arrangementId != null)
				return false;
		} else if (!arrangementId.equals(other.arrangementId))
			return false;
		if (extensionData == null) {
			if (other.extensionData != null)
				return false;
		} else if (!extensionData.equals(other.extensionData))
			return false;
		return true;
	}

}
