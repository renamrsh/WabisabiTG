package com.example.wabisabi.Domain;

import java.util.ArrayList;
import java.util.List;

public class GroupsDomain {
    private String groupTitle;
    private String groupDescription;
    private String groupIcon;

    private long timestamp;
    private String groupId;
    private List<String> participantIds;
    private String createdBy;
    private int participantCount;

    public GroupsDomain(String groupId, String groupTitle, String groupDescription) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.participantIds = new ArrayList<>();
    }

    public GroupsDomain(String groupId, String groupTitle, String groupDescription, ArrayList<String> participantIds) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.participantIds = participantIds;
    }
    public GroupsDomain() {
        this.participantIds = new ArrayList<>();

    }

    public GroupsDomain(String groupId, String groupTitle, String groupDescription, String groupIcon) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescription = groupDescription;
        this.groupIcon = groupIcon;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupsName) {
        this.groupTitle = groupsName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupsDescription) {
        this.groupDescription = groupsDescription;
    }

    public String getGroupsPicAddress() {
        return groupIcon;
    }
    public String getGroupIcon() {
        return groupIcon;
    }
    public void setGroupsPicAddress(String groupsPicAddress) {
        this.groupIcon = groupsPicAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void addParticipantId(String participantId) {
        participantIds.add(participantId);
    }

    public void removeParticipantId(String participantId) {
        participantIds.remove(participantId);
    }
    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public int getParticipantCount() {
        return participantCount;
    }
    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}