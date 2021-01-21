import React from 'react';
import ActionCable from 'actioncable';
import { API_WS_ROOT } from './constants';

export default class JavaIde extends React.Component {
  
  constructor(props) {
    super(props);
    console.log(this.props);

    this.state = {
      connectedToChannel: false,
      programOutput: [],
      connection: null,
      program: null,
      filename: null,
      userInput: null,
    };
  }

  componentDidMount() {
    this.setUpCable();
  }

  onConnected = () => {
    console.log("connected to channel " + this.props.userId);
    this.setState({connectedToChannel: true});
  }

  onReceived = (data) => {
    console.log(data);
    const stdout = data.stdout
    // const stderr = data.stderr
    // const status = data.status
    let output = [stdout]
    if (data.stderr) {
      output.push(`Error: ${data.stderr}`)
    }
    this.setState({programOutput: [...this.state.programOutput, output]})
  }


  setUpCable = () => {
    const cable = ActionCable.createConsumer(API_WS_ROOT);
    const userId = this.props.userId;
    const onConnected = this.onConnected
    const onReceived = this.onReceived
    const connection = cable.subscriptions.create({channel: "JavaRunnerChannel", user_id: userId}, {
      connected() {
        // console.log("connected to channel " + userId);
        // setState({connectedToChannel: true});
        onConnected();
      },
  
      disconnected() {
        // Called when the subscription has been terminated by the server
      },
  
      received(data) {
        onReceived(data);
      },
  
      run_program(filename, text) {
        console.log("calling run program");
        this.perform("run_program", {filename: filename, text: text, user_id: userId})
      },

      send_input(text) {
        this.perform("send_input", {data: text, user_id: userId})
      }
    });

    this.setState({connection: connection});
  }

  handleSubmit = (event) => {
    event.preventDefault();
    if (!this.state.filename || !this.state.program || !this.state.connection) {
      console.log("something is wrong...");
      return;
    }
    this.state.connection.run_program(this.state.filename, this.state.program);
  }

  handleInputChange = (event) => {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
      [name]: value    
    });
  }

  handleUserInputSubmit = (event) => {
    event.preventDefault();
    if (!this.state.userInput || !this.state.connection) {
      console.log("something is wrong...");
      return;
    }
    this.state.connection.send_input(this.state.userInput)
  }

  render() {
    return (
      <div style={{marginLeft: 15}}>
        <h1>Basic Java IDE</h1>
        <form onSubmit={this.handleSubmit}>
          <label>Your program
            <br/>
            <textarea rows={10} cols={80} name="program" onChange={this.handleInputChange}/>
          </label>
          <br/>
          <br/>
          <label>Filename<br/>
            <input type="text" name="filename" onChange={this.handleInputChange}/>
          </label>
          <br/>
          <br/>
          <input type="submit" value="Run My Program!"/>
        </form>
        <br/>
        <form onSubmit={this.handleUserInputSubmit}>
          <label>Stdin <br/>
            <textarea rows={3} cols={80} name="userInput" onChange={this.handleInputChange}/>
          </label>
          <br/>
          <br/>
          <input type="submit" value="Add User Input"/>
        </form>
        <br/>
        <h2>Output</h2>
        <div>
          {this.state.programOutput.map((output) => (
            <p>{output}</p>
          ))}
        </div>
        
      </div>
    );
  }
}