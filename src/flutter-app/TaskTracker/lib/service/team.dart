/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

import 'package:TaskTracker/common/utf8.utils.dart';

class Team {

  int id;
  String name = '';
  String description = '';
  bool active = false;
  List<int> userIDs = [];
  List<int> teamLeaderIDs = [];
  List<String> teamLeaderNames = [];

  Team();

  factory Team.fromMap(final Map<String, dynamic> fields) {
    Team team = Team();
    team.id = fields['id'];
    team.name = Utf8Utils.fromUtf8(fields['name']);
    team.description = Utf8Utils.fromUtf8(fields['description']);
    if (fields.containsKey('active')) {
      team.active = fields['active'];
    }
    if (fields.containsKey('userIDs')) {
      team.userIDs = List.from(fields['userIDs']);
    }
    if (fields.containsKey('teamLeaderIDs')) {
      team.teamLeaderIDs = List.from(fields['teamLeaderIDs']);
    }
    if (fields.containsKey('teamLeaderNames')) {
      team.teamLeaderNames = List.from(fields['teamLeaderNames']);
    }
    return team;
  }

  factory Team.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return Team.fromMap(fields);
  }

  static List<Team> listFromJsonString(final String jsonString) {
    List<Team> teamList = List<Team>();
    dynamic teams = jsonDecode(jsonString);
    teams.forEach((element) {
      teamList.add(Team.fromMap(element));
    });
    return teamList;
  }

  Map toJson() {
    return {
      'id' : id,
      'name' : name,
      'description' : description,
      'active' : active,
      'userIDs' : userIDs?.toList(),
      'teamLeaderIDs' : teamLeaderIDs?.toList(),
    };
  }
}
