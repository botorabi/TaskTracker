/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class Team {

  int id;
  String name = '';
  String description = '';
  bool active = false;
  List<int> users = [];

  Team();

  factory Team.fromMap(final Map<String, dynamic> fields) {
    Team team = Team();
    team.id = fields['id'];
    team.name = fields['name'];
    team.description = fields['description'];
    if (fields.containsKey('active')) {
      team.active = fields['active'];
    }
    team.users = List.from(fields['users']);
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
      'users' : users.toList()
    };
  }
}
