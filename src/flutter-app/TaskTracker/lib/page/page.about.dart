/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'package:TaskTracker/config.dart';
import 'package:TaskTracker/page/pagefooter.dart';
import 'package:TaskTracker/translator.dart';
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';


class PageAbout extends StatefulWidget {
  final String title;

  PageAbout({Key key, this.title}) : super(key: key);

  @override
  _PageAboutState createState() => _PageAboutState();
}

class _PageAboutState extends State<PageAbout> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      persistentFooterButtons: PageFooter.build(),
      body: ListView(
        shrinkWrap: true,
        children: [
          Center(
            child: Container(
              constraints: BoxConstraints(maxWidth: Config.DEFAULT_PANEL_WIDTH),
              padding: const EdgeInsets.all(40.0),
              child: Card(
                child: Container(
                  padding: const EdgeInsets.all(20.0),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Image(image: AssetImage('images/logo.png')),
                      SizedBox(width: 20),
                      Column(
                        children: [
                          Text(Translator.text('AppTaskTracker', 'Task Tracker')),
                          SizedBox(height: 10),
                          Text(Translator.text('Common', 'Version') + ' ' + Config.appInfo.version),
                          SizedBox(height: 25),
                          Text("Copyright (c) 2020"),
                          SizedBox(height: 15),
                          RaisedButton(
                            onPressed: () => launch('https://github.com/botorabi/TaskTracker'),
                            child: Text('https://github.com/botorabi/TaskTracker'),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
