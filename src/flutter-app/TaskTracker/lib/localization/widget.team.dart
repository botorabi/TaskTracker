/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/service/task.dart';
import 'package:flutter/services.dart';

const Translations_WidgetTeam = {
  'New Team': {
    'en': 'New Team',
    'de': 'Neues Team',
  },
  'Add New Team': {
    'en': 'Add New Team',
    'de': 'Neues Team hinzufügen',
  },
  'Choose a team name': {
    'en': 'Choose a team name',
    'de': 'Teamnamen wählen',
  },
  'New team was successfully created.': {
    'en': 'New team was successfully created.',
    'de': 'Neues Team wurde erfolgreich angelegt.',
  },
  'Could not create new team!\nReason: A team with given name already exists.': {
    'en': 'Could not create new team!\nReason: A team with given name already exists.',
    'de': 'Neues Team konnte nicht angelegt werden! Grund: Ein Team mit dem angegebenen Namen existiert bereits.',
  },
  'Could not create new team!\nReason: ': {
    'en': 'Could not create new team!\nReason: ',
    'de': 'Neues Team konnte nicht angelegt werden!\nGrund: ',
  },
  'Failed to retrieve teams, reason: ': {
    'en': 'Failed to retrieve teams! Reason: ',
    'de': 'Teams konnten nicht ausgelesen werden! Grund: ',
  },
  'Edit Team': {
    'en': 'Edit Team',
    'de': 'Team bearbeiten',
  },
  'Edit Team Settings': {
    'en': 'Edit Team Settings',
    'de': 'Teameinstellungen bearbeiten',
  },
  'Team Deletion': {
    'en': 'Team Deletion',
    'de': 'Team-Löschung',
  },
  'Do you really want to delete team ': {
    'en': 'Do you really want to delete team ',
    'de': 'Soll dieses Team wirklich gelöscht werden: ',
  },
  'Team was successfully deleted.': {
    'en': 'Team was successfully deleted.',
    'de': 'Team wurde erfolgreich gelöscht.',
  },
  'Failed to delete team, reason: ': {
    'en': 'Failed to delete team, reason: ',
    'de': 'Team konnte nicht gelöscht werden, Grund: ',
  },
  'Could not retrieve team! Reason: ': {
    'en': 'Could not retrieve team! Reason: ',
    'de': 'Team konnte nicht ausgelesen werden! Grund: ',
  },
  'Team Members' :{
    'en': 'Team Members',
    'de': 'Teammitglieder',
  },
  'Team Leaders' :{
    'en': 'Team Leaders',
    'de': 'Teamleitungen',
  },
  'Find Team' :{
    'en': 'Find Team',
    'de': 'Teams finden',
  },
  'Found Teams' :{
    'en': 'Found Teams',
    'de': 'Gefundene Teams',
  },
  'Chosen Teams' :{
    'en': 'Chosen Teams',
    'de': 'Ausgewählte Teams',
  },
  'Found Users' : {
    'en': 'Found Users',
    'de': 'Gefundene Benutzer',
  },
  'Chosen Users' : {
    'en': 'Chosen Users',
    'de': 'Ausgewählte Benutzer'
  },
};
