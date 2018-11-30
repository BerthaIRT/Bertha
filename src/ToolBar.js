import React, { Component } from 'react';
import './ToolBar.css';
import { TabsContainer, Tabs, Tab, ListItem, MenuButton,
Grid, Cell } from 'react-md';
import ImageGallery from 'react-image-gallery';
import "react-image-gallery/styles/css/image-gallery.css";
import 'react-image-gallery/styles/scss//image-gallery.scss';

export default class ToolBar extends Component {
  render() {
    const images = [
        {
          srcSet: "shelby.jpg",
          thumbnail: 'http://lorempixel.com/250/150/nature/1/',
          sizes: "(min-width: 36em) 33.3vw,100vw"
        },
        {
          original: 'shelby',
          thumbnail: 'http://lorempixel.com/250/150/nature/2/'
         
        },
        {
          original: '',
          thumbnail: 'http://lorempixel.com/250/150/nature/3/'
    
        }
      ]

    return (
      <TabsContainer panelClassName="md-grid" fixed colored defaultTabIndex={0} centered >
            <Tabs tabId="simple-tab">
                    <Tab label="About Bertha">
                        <Grid>
                            <Cell size={12}>
                            <div className="thisText">BERTHA IRT is a tool which Students, Parents, and Teachers can use to report an incident anonymously.
                             Students join groups using an Access Code generated when an administrator creates a group.
                              Administrators are given tools to manage reports and keep the sender updated.</div>
                            </Cell>
                            
                        </Grid>
                    </Tab>
                    <Tab label="Pictures">
                        <Grid>
                            <Cell size={12}>
                                <ImageGallery items={images}></ImageGallery>
                            </Cell>
                        </Grid>
                    </Tab>
                    <Tab label="Demo">
                        <Grid>
                            <Cell size={12}>
                            <div className="thisText">Bajjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjking Cakes and Cookies and creme and logic</div>
                            </Cell>
                        </Grid>
                    </Tab>
                    <Tab label="Deliverables" >
                        <Grid>
                            <Cell size={12}>
                            <div className="thisText">BERTHA IRT is a tool which Students, Parents, and Teachers can use to report an incident anonymously.
                            Students join groups using an Access Code generated when an administrator creates a group.
                            Administrators are given tools to manage reports and keep the sender updated.</div>
                            </Cell>
                        </Grid>
                    </Tab>
            </Tabs>
      </TabsContainer>
    )
  }
}
