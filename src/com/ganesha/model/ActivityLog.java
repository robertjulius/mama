package com.ganesha.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ganesha.coreapps.constants.Enums.ActionType;

@Entity
@Table(name = "ACTIVITY_LOGS")
public class ActivityLog implements TableEntity {

	private static final long serialVersionUID = 2343345295596584661L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Integer id;

	@Column(name = "USER_ID", nullable = false)
	private Integer userId;

	@Column(name = "USER_LOGIN_ID", nullable = false)
	private String userLoginId;

	@Column(name = "USER_NAME", nullable = false)
	private String userName;

	@Column(name = "PERMISSION_CODE", nullable = false)
	private String permissionCode;

	@Column(name = "PERMISSION_NAME", nullable = false)
	private String permissionName;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private ActionType actionType;

	@Column(name = "ENTITY_CLASS", nullable = false)
	private String entityClass;

	@Column(name = "ENTITY_ID", nullable = false)
	private Integer entityId;

	@Column(name = "ACTION_TIMESTAMP", nullable = false)
	private Timestamp actionTimestamp;

	public Timestamp getActionTimestamp() {
		return actionTimestamp;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public Integer getId() {
		return id;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getUserLoginId() {
		return userLoginId;
	}

	public String getUserName() {
		return userName;
	}

	public void setActionTimestamp(Timestamp actionTimestamp) {
		this.actionTimestamp = actionTimestamp;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
