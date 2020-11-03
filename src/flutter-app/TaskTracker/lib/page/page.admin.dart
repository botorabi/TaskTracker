/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/translator.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.reportmail.configurationlist.dart';
import 'package:TaskTracker/widget/widget.tasklist.dart';
import 'package:TaskTracker/widget/widget.teamlist.dart';
import 'package:TaskTracker/widget/widget.userlist.dart';
import 'package:flutter/material.dart';


class PageAdmin extends StatefulWidget {
  final String title;

  PageAdmin({Key key, this.title}) : super(key: key);

  @override
  _PageAdminState createState() => _PageAdminState();
}

class _PageAdminState extends State<PageAdmin> with SingleTickerProviderStateMixin {

  TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this, length: 4, initialIndex: 0);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: Center(
        child: Container(
          constraints: BoxConstraints(maxWidth: Config.DEFAULT_PANEL_WIDTH),
          padding: const EdgeInsets.all(20.0),
          child: Column (
            children: [
              TabBar(
                controller: _tabController,
                labelColor: Colors.black,
                indicatorWeight: 4.0,
                tabs: [
                  Tab(text: Translator.text('Common', 'Tasks')),
                  Tab(text: Translator.text('Common', 'Teams')),
                  Tab(text: Translator.text('Common', 'Users')),
                  Tab(text: Translator.text('Common', 'Report')),
                ],
              ),
              Expanded(child: TabBarView(
                controller: _tabController,
                children: [
                  WidgetTaskList(title: ''),
                  WidgetTeamList(title: ''),
                  WidgetUserList(title: ''),
                  WidgetReportMailConfigurationList(title: ''),
                 ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
