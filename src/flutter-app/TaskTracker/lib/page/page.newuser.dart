/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/page/pagedrawer.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/widget/widget.useredit.dart';
import 'package:flutter/material.dart';

class PageNewUser extends StatefulWidget {
  final String title;

  PageNewUser({Key key, this.title}) : super(key: key);

  @override
  _PageNewUserState createState() => _PageNewUserState();
}

class _PageNewUserState extends State<PageNewUser> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading: PageDrawer.buildNavigateBack(),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: WidgetUserEdit(userId: 0)
          ),
        ],
      ),
    );
  }
}
